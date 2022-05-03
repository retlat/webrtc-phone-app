package com.example.phoneapp.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.example.phoneapp.resource.R
import com.example.phoneapp.ui.theme.PhoneAppTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val isMicAccessPermitted = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val launcher = registerForActivityResult(RequestPermission()) { isGranted ->
            isMicAccessPermitted.value = isGranted
        }

        setContent {
            PhoneAppTheme {
                Scaffold(topBar = {
                    TopAppBar(title = {
                        Text(text = stringResource(id = R.string.app_name))
                    })
                }) {
                    MainScreen(
                        isMicAccessPermitted = isMicAccessPermitted.collectAsState(),
                        onClickRequestPermission = {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        isMicAccessPermitted.value = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
