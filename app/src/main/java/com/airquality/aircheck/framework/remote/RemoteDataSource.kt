package com.airquality.aircheck.framework.remote

import android.content.Context
import com.airquality.aircheck.data.datasource.remote.AirQualityApiService
import com.airquality.aircheck.data.datasource.remote.AirQualityResponseDto
import com.airquality.aircheck.ui.screens.home.utils.getCity
import com.airquality.domain.AirCoordinates
import com.airquality.domain.AirParameter
import com.airquality.domain.HomeDataModel
import com.airquality.domain.datasource.ILocationAirQualityDataSource

class RemoteDataSource(
    private val apiService: AirQualityApiService,
    private val context: Context
): ILocationAirQualityDataSource {

    override suspend fun getNearestAirQuality(latitude: Double, longitude: Double): HomeDataModel {
        val apiQualityResponse = apiService.fetchAirQuality(latitude, longitude)
        val city = context.getCity(latitude, longitude)
        return apiQualityResponse.toDomainModel(city)
    }
}
 private fun AirQualityResponseDto.toDomainModel(city: String): HomeDataModel =
     HomeDataModel(
         city = city,
         parameters = listOf(
             AirParameter("PM10", current.pm10, units.pm10),
             AirParameter("PM2.5", current.pm25, units.pm25),
             AirParameter("CO", current.carbonMonoxide, units.carbonMonoxide),
             AirParameter("Ozone", current.ozone, units.ozone),
             AirParameter("NO2", current.nitrogenDioxide, units.nitrogenDioxide),
             AirParameter("SO2", current.sulphurDioxide, units.sulphurDioxide)
         ).filter { it.lastValue >= 0 },
         coordinates = AirCoordinates(latitude, longitude),
         lastUpdated = current.time
     )