package com.airquality.domain.datasource

import com.airquality.domain.HomeDataModel


interface ILocationAirQualityDataSource {
    suspend fun getAirQuality(latitude: Double, longitude: Double): HomeDataModel
    suspend fun getHistoricAirQuality(latitude: Double, longitude: Double): HistoricForecastDataModel
    suspend fun getForecastAirQuality(latitude: Double, longitude: Double): HistoricForecastDataModel
}