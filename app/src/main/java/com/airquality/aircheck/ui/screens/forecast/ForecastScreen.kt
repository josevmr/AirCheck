package com.airquality.aircheck.ui.screens.forecast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airquality.aircheck.ui.screens.home.rememberHomeState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForecastScreen(
    vm: ForecastViewModel = koinViewModel()
) {
    val state by vm.forecastState.collectAsState()
    val forecastState = rememberHomeState()

    LaunchedEffect(Unit) {
        vm.onUiReady()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 32.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Text(text = "Ciudad: ${state.data.city} / AQI medio: ${state.data.averageAQI}")
                }
            }
        }
    }
}