package com.airquality.aircheck.framework.remote

import android.content.Context
import com.airquality.aircheck.data.datasource.remote.AirHistoricForecastResponseDto
import com.airquality.aircheck.data.datasource.remote.AirQualityApiService
import com.airquality.aircheck.data.datasource.remote.AirQualityResponseDto
import com.airquality.aircheck.ui.screens.home.utils.getCity
import com.airquality.domain.AirCoordinates
import com.airquality.domain.AirParameter
import com.airquality.domain.HomeDataModel
import com.airquality.domain.datasource.AirParameterHistoricForecast
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.domain.datasource.ILocationAirQualityDataSource

class RemoteDataSource(
    private val apiService: AirQualityApiService,
    private val context: Context
): ILocationAirQualityDataSource {

    override suspend fun getAirQuality(latitude: Double, longitude: Double): HomeDataModel {
        val apiQualityResponse = apiService.fetchAirQuality(latitude, longitude)
        val city = context.getCity(latitude, longitude)
        return apiQualityResponse.toDomainModel(city)
    }

    override suspend fun getHistoricAirQuality(latitude: Double, longitude: Double): HistoricForecastDataModel {
        val apiHistoricResponse = apiService.getHistoryQuality(latitude, longitude)
        val city = context.getCity(latitude, longitude)
        return apiHistoricResponse.toDomainModel(city)
    }

    override suspend fun getForecastAirQuality(latitude: Double, longitude: Double): HistoricForecastDataModel {
        val apiForecastResponse = apiService.getForecastQuality(latitude, longitude)
        val city = context.getCity(latitude, longitude)
        return apiForecastResponse.toDomainModel(city)
    }


}

// Extension fun for HomeScreen
private fun AirQualityResponseDto.toDomainModel(city: String): HomeDataModel =
     HomeDataModel(
         city = city,
         parameters = listOf(
             AirParameter("PM 10", current.pm10, units.pm10),
             AirParameter("PM 2.5", current.pm25, units.pm25),
             AirParameter("CO", convertCO(current.carbonMonoxide), "mg/m³"),
             AirParameter("O3", current.ozone, units.ozone),
             AirParameter("NO2", current.nitrogenDioxide, units.nitrogenDioxide),
             AirParameter("SO2", current.sulphurDioxide, units.sulphurDioxide)
         ).filter { it.lastValue >= 0 },
         coordinates = AirCoordinates(latitude, longitude),
         lastUpdated = current.time
     )

private fun convertCO(value: Double): Double {
    return value / 1000 // Convert from μg/m³ to mg/m³
}

// Extension fun for Historic and Forecast Screens
private fun AirHistoricForecastResponseDto.toDomainModel(city: String): HistoricForecastDataModel {
    val result = hourly.time.indices.map { index ->
        AirParameterHistoricForecast(
            time = hourly.time[index],
            values = mapOf(
                "PM 10" to (hourly.pm10.getOrNull(index) ?: -1.0),
                "PM 2.5" to (hourly.pm25.getOrNull(index) ?: -1.0),
                "CO" to convertCO(hourly.carbonMonoxide.getOrNull(index) ?: -1.0),
                "O3" to (hourly.ozone.getOrNull(index) ?: -1.0),
                "NO2" to (hourly.nitrogenDioxide.getOrNull(index) ?: -1.0),
                "SO2" to (hourly.sulphurDioxide.getOrNull(index) ?: -1.0)
            ).filterValues { it >= 0 }
        )
    }

    val averageAQI = calculateAirQualityIndex(result.map { it.values })

    return HistoricForecastDataModel(
        city = city,
        parameters = result,
        averageAQI = averageAQI
    )
}

private fun calculateAirQualityIndex(values: List<Map<String, Double>>): Double {
    val worstPerHour = values.map { it.values.maxOrNull() ?: 0.0 }
    return worstPerHour.average()
}
