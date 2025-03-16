package com.airquality.data

import com.airquality.domain.HomeDataModel
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

    fun getNearestAirQuality(): Flow<HomeDataModel> = flow{
        val coordinates = locationRepository.findLastLocation()
        var data = remoteDataSource.getNearestAirQuality(
            coordinates?.latitude ?: DEFAULT_LATITUDE,
            coordinates?.longitude ?: DEFAULT_LONGITUDE)
        emit(data)
    }
}