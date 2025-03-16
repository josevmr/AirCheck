package com.airquality.data

import com.airquality.domain.AirCoordinates
import com.airquality.domain.datasource.ILocationDataSource
import org.koin.core.annotation.Factory

@Factory
class LocationRepository(
    private val locationDataSource: ILocationDataSource
) {
    suspend fun findLastLocation(): AirCoordinates? = locationDataSource.findLastLocation()
}