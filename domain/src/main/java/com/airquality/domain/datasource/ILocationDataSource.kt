package com.airquality.domain.datasource

import com.airquality.domain.AirCoordinates

interface ILocationDataSource {
    suspend fun findLastLocation(): AirCoordinates?
}