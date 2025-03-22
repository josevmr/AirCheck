package com.airquality.aircheck.ui.screens.home

import android.content.Context
import android.location.LocationManager
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

    fun onGPSDisabled() {
        _homeState.update {
            it.copy(
                message = "Para obtener la calidad del aire en tu ubicación, activa el GPS."
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

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun getParameterDescription(parameter: String, context: Context): Pair<String, String> {
        return when (parameter) {
            "PM 10" -> Pair(
                context.getString(R.string.pm10_title),
                context.getString(R.string.pm10_description)
            )

            "PM 2.5" -> Pair(
                context.getString(R.string.pm25_title),
                context.getString(R.string.pm25_description)
            )

            "NO₂" -> Pair(
                context.getString(R.string.no2_title),
                context.getString(R.string.no2_description)
            )

            "O₃" -> Pair(
                context.getString(R.string.ozone_title),
                context.getString(R.string.ozone_description)
            )

            "CO" -> Pair(
                context.getString(R.string.co_title),
                context.getString(R.string.co_description)
            )

            "SO₂" -> Pair(
                context.getString(R.string.so2_title),
                context.getString(R.string.so2_description)
            )

            else -> Pair("", "")
        }
    }
}