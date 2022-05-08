package main

import (
	"context"
	"encoding/json"
	"io"
	"log"
	"net/http"
	"time"

	firebase "firebase.google.com/go/v4"
	"firebase.google.com/go/v4/messaging"
	"golang.org/x/net/websocket"
)

type PushToken string

type WSBody struct {
	Type    string          `json:"type"`
	Payload json.RawMessage `json:"payload"`
}

type WSCallPayload struct {
	Token PushToken `json:"token"`
}

type RegisterBody struct {
	Token PushToken `json:"token"`
}

type TokensResponse struct {
	Tokens []PushToken `json:"tokens"`
}

func main() {
	push := make(chan PushToken)
	tokens := make([]PushToken, 0)
	var connections []*websocket.Conn

	go func() {
		app, err := firebase.NewApp(context.Background(), nil)
		if err != nil {
			log.Printf("Failed to init Firebase App: %v\n", err)
			return
		}

		ctx := context.Background()
		client, err := app.Messaging(ctx)
		if err != nil {
			log.Printf("Failed to create messaging instance: %v\n", err)
			return
		}

		ttl := 0 * time.Second

		for {
			token := <-push
			_, err = client.Send(ctx, &messaging.Message{
				Data: map[string]string{
					"type": "call",
				},
				Android: &messaging.AndroidConfig{
					TTL: &ttl,
				},
				APNS: &messaging.APNSConfig{
					Headers: map[string]string{
						// 2021-02 に使った時はこれを設定していると
						// バリデーションエラーになった記憶があるので
						// 今も APNS に直接リクエストしないとダメかも
						"apns-push-type":  "voip",
						"apns-topic":      "com.example.PhoneApp.voip",
						"apns-expiration": "0",
					},
				},
				Token: string(token),
			})
			if err != nil {
				log.Printf("Failed to send a message to FCM: %v\n", err)
			}
		}
	}()

	http.Handle("/ws", websocket.Handler(func(ws *websocket.Conn) {
		connections = append(connections, ws)
		for {
			var m WSBody
			err := websocket.JSON.Receive(ws, &m)
			if err != nil {
				if err == io.EOF {
					for i, conn := range connections {
						if conn == ws {
							connections = append(connections[:i], connections[i+1:]...)
							break
						}
					}
					return
				}
				log.Printf("Failed to receive: %v\n", err)
				continue
			}
			if m.Type == "call" {
				var p WSCallPayload
				err = json.Unmarshal(m.Payload, &p)
				if err != nil {
					log.Printf("Failed to unmarshal call payload: %v\n", err)
					continue
				}
				push <- p.Token
				continue
			}
			if m.Type == "signalling" {
				for _, conn := range connections {
					if conn == ws {
						continue
					}
					err = websocket.JSON.Send(conn, m)
					if err != nil {
						log.Printf("Failed to send signalling message: %v\n", err)
					}
					break
				}
				continue
			}
		}
	}))

	http.HandleFunc("/register", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != "POST" || r.Header.Get("Content-Type") != "application/json" {
			w.WriteHeader(http.StatusBadRequest)
			return
		}

		var payload RegisterBody
		err := json.NewDecoder(r.Body).Decode(&payload)
		if err != nil {
			log.Printf("Failed to decode register payload: %v\n", err)
			w.WriteHeader(http.StatusInternalServerError)
			return
		}

		e := false
		for _, token := range tokens {
			if token == payload.Token {
				e = true
				break
			}
		}
		if !e {
			tokens = append(tokens, payload.Token)
		}

		w.WriteHeader(http.StatusOK)
	})

	http.HandleFunc("/tokens", func(w http.ResponseWriter, r *http.Request) {
		j, err := json.Marshal(TokensResponse{tokens})
		if err != nil {
			log.Printf("Failed to marshal tokens response: %v\n", err)
			w.WriteHeader(500)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		_, _ = w.Write(j)
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}
