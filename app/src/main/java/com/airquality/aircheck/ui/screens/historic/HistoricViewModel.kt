package com.airquality.aircheck.ui.screens.historic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.usecases.GetHistoricAirQualityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HistoricViewModel(
    private val getHistoricAirQualityUseCase: GetHistoricAirQualityUseCase
) : ViewModel() {

    private val _historicState = MutableStateFlow(UiState())
    val historicState: StateFlow<UiState> = _historicState

    data class UiState(
        val isLoading: Boolean = true,
        val data: HistoricForecastDataModel = HistoricForecastDataModel(),
    )

    fun onUiReady() {
        viewModelScope.launch {
            getHistoricAirQualityUseCase.invoke().collect {
                _historicState.update { state ->
                    state.copy(
                        isLoading = false,
                        data = it
                    )
                }
            }
        }
    }
}