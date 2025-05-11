package com.airquality.aircheck.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class HomeState(
    private val context: Context
) {
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}

@Composable
fun rememberHomeState(
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
): HomeState {
    val context = LocalContext.current
    return remember(snackBarHostState) { HomeState(context) }
}
