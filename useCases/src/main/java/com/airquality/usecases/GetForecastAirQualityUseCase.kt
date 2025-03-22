package com.airquality.usecases

import com.airquality.data.LocationAirQualityRepository
import com.airquality.domain.datasource.HistoricForecastDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class GetForecastAirQualityUseCase(
    private val locationRepository: LocationAirQualityRepository
) {
    operator fun invoke(): Flow<HistoricForecastDataModel> = flow {
        try {
            val data = locationRepository.getForecastAirQuality().first()
            emit(data)
        } catch (e: Exception) {
            //tratar el catch
        }
    }
}