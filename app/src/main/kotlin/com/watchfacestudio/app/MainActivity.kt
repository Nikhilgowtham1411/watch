package com.watchfacestudio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.watchfacestudio.app.navigation.AppNavigation
import com.watchfacestudio.app.ui.theme.WatchFaceStudioTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WatchFaceStudioTheme {
                AppNavigation()
            }
        }
    }
}
