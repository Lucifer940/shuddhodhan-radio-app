package com.radioshuddhodhan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.radioshuddhodhan.app.ui.AppRoot

/**
 * Single-activity architecture: this is the ONLY activity in the app.
 * All screens are Jetpack Compose destinations inside one NavHost.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val systemSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hold the system splash until Compose has laid out the first frame.
        var uiReady = false
        systemSplash.setKeepOnScreenCondition { !uiReady }

        setContent {
            uiReady = true
            val app = application as RadioApp
            AppRoot(app = app)
        }
    }
}
