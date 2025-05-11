package com.airquality.aircheck.ui.screens.forecast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airquality.domain.datasource.AirParameterHistoricForecast
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.usecases.GetForecastAirQualityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        viewModelScope.launch(Dispatchers.IO) {
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

    fun groupByParameter(items: List<AirParameterHistoricForecast>): Map<String, List<Pair<String, Double>>> {
        val grouped = mutableMapOf<String, MutableList<Pair<String, Double>>>()

        items.forEach { item ->
            val hour = item.time.takeLast(5).dropLast(3) // "2025-03-25T02:00" -> "02"
            item.values.forEach { (param, value) ->
                grouped.getOrPut(param) { mutableListOf() }.add(hour to value)
            }
        }

        return grouped
    }

    fun formatToUserDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString.substringBefore("T"))
            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (e: Exception) {
            dateString
        }
    }

    fun groupByDay(list: List<AirParameterHistoricForecast>): Map<String, List<AirParameterHistoricForecast>> {
        return list.groupBy { it.time.substringBefore("T") }
    }

    fun getParameterUnits(parameter: String): String {
        return when (parameter) {
            "CO" -> "mg/m³"
            else -> "μg/m³"
        }
    }
}