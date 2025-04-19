package com.airquality.usecases

import com.airquality.data.LocationAirQualityRepository
import com.airquality.domain.HomeDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class FetchAirQualityUseCase(
    private val locationRepository: LocationAirQualityRepository
) {
    operator fun invoke(): Flow<HomeDataModel> = flow {
        try {
            val data = locationRepository.getAirQuality().first()
            emit(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}