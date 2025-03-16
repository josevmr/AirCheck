package com.airquality.aircheck.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airquality.aircheck.R
import com.airquality.domain.HomeDataModel
import com.airquality.shared.calculateAirQualityIndex
import com.airquality.usecases.FetchAirQualityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val fetchAirQualityUseCase: FetchAirQualityUseCase
) : ViewModel() {

    private val _homeState = MutableStateFlow(UiState())
    val homeState: StateFlow<UiState> = _homeState

    data class UiState(
        val isLoading: Boolean = true,
        val data: HomeDataModel = HomeDataModel(),
        val message: String = "",
        val isPermissionDeniedVisible: Boolean = false,
        val hasPermission: Boolean = false
    )

    fun onUiReady() {
        viewModelScope.launch {
            fetchAirQualityUseCase.invoke().collect {
                _homeState.update { state ->
                    state.copy(
                        isLoading = false,
                        data = it
                    )
                }
            }
        }
    }

    fun onPermissionDenied() {
        _homeState.update {
            it.copy(
                message = "Necesitas dar permisos para usar la aplicación",
                isPermissionDeniedVisible = true
            )
        }
    }

    fun onMessageShown() {
        _homeState.update {
            it.copy(
                message = "",
                isPermissionDeniedVisible = false
            )
        }
    }

    fun onSettingsButtonClicked() {
        _homeState.update {
            it.copy(
                isPermissionDeniedVisible = false
            )
        }
    }

    fun obtainAirQualityIndex(): Double {
        return calculateAirQualityIndex(_homeState.value.data)
    }

    fun getDescriptions() = mapOf(
        Pair(R.string.pm25_title, R.string.pm25_description),
        Pair(R.string.pm10_title, R.string.pm10_description),
        Pair(R.string.ozone_title, R.string.ozone_description),
        Pair(R.string.no_title, R.string.no_description),
        Pair(R.string.no2_title, R.string.no2_description),
        Pair(R.string.so2_title, R.string.so2_description),
        Pair(R.string.co_title, R.string.co_description),
        Pair(R.string.bc_title, R.string.bc_description)
    )

    fun getParameterTitle(parameter: String, context: Context): String {
        return when (parameter) {
            "um003" -> context.getString(R.string.um003_title_with_unit)
            "pm1" -> context.getString(R.string.pm1_title_with_unit)
            "PM10" -> context.getString(R.string.pm10_title_with_unit)
            "relativehumidity" -> context.getString(R.string.relative_humidity_title_with_unit)
            "temperature" -> context.getString(R.string.temperature_with_unit)
            "PM2.5" -> context.getString(R.string.pm25_title_with_unit)
            "NO2" -> context.getString(R.string.no2_title_with_unit)
            "nox" -> context.getString(R.string.nox_title_with_unit)
            "no" -> context.getString(R.string.no_title_with_unit)
            "Ozone" -> context.getString(R.string.o3_title_with_unit)
            "CO" -> context.getString(R.string.co_title_with_unit)
            "SO2" -> context.getString(R.string.so2_title_with_unit)
            else -> parameter
        }
    }
}