package com.airquality.aircheck

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.airquality.aircheck.ui.screens.MainScreen
import com.airquality.aircheck.ui.screens.home.utils.GradientBackground
import com.airquality.aircheck.ui.theme.AirCheckTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AirCheckTheme {
                GradientBackground {
                    MainScreen()
                }
            }
        }
    }
}