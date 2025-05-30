package com.airquality.data

import com.airquality.domain.HomeDataModel
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.domain.datasource.ILocationAirQualityDataSource
import com.airquality.shared.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class LocationAirQualityRepository(
    private val remoteDataSource: ILocationAirQualityDataSource,
    private val locationRepository: LocationRepository
) {

    fun getAirQuality(): Flow<HomeDataModel> = flow{
        val coordinates = locationRepository.findLastLocation()
        val data = remoteDataSource.getAirQuality(
            coordinates?.latitude ?: DEFAULT_LATITUDE,
            coordinates?.longitude ?: DEFAULT_LONGITUDE)
        emit(data)
    }

    fun getHistoricAirQuality(): Flow<HistoricForecastDataModel> = flow {
        emit(fetchData(remoteDataSource::getHistoricAirQuality))
    }

    fun getForecastAirQuality(): Flow<HistoricForecastDataModel> = flow {
        emit(fetchData(remoteDataSource::getForecastAirQuality))
    }

    private suspend fun fetchData(
        fetch: suspend (lat: Double, lon: Double) -> HistoricForecastDataModel
    ): HistoricForecastDataModel {
        val coordinates = locationRepository.findLastLocation()
        return fetch(
            coordinates?.latitude ?: DEFAULT_LATITUDE,
            coordinates?.longitude ?: DEFAULT_LONGITUDE
        )
    }
}
