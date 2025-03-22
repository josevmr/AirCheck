package com.airquality.aircheck.ui.screens.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.usecases.GetForecastAirQualityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ForecastViewModel(
    private val getForecastAirQualityUseCase: GetForecastAirQualityUseCase
) : ViewModel() {

    private val _forecastState = MutableStateFlow(UiState())
    val forecastState: StateFlow<UiState> = _forecastState

    data class UiState(
        val isLoading: Boolean = true,
        val data: HistoricForecastDataModel = HistoricForecastDataModel(),
    )

    fun onUiReady() {
        viewModelScope.launch {
            getForecastAirQualityUseCase.invoke().collect {
                _forecastState.update { state ->
                    state.copy(
                        isLoading = false,
                        data = it
                    )
                }
            }
        }
    }
}