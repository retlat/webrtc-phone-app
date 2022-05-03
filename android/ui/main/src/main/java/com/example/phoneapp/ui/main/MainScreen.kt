package com.example.phoneapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(isMicAccessPermitted: State<Boolean>, onClickRequestPermission: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.ui_main_mic_state))
            if (isMicAccessPermitted.value) {
                Text(stringResource(R.string.ui_main_mic_permitted))
            } else {
                Button(onClick = { onClickRequestPermission() }) {
                    Text(stringResource(R.string.ui_main_mic_request))
                }
            }
        }
    }
}
