package com.airquality.domain.datasource

import com.airquality.domain.HomeDataModel


interface ILocationAirQualityDataSource {
    suspend fun getNearestAirQuality(latitude: Double, longitude: Double): HomeDataModel
}