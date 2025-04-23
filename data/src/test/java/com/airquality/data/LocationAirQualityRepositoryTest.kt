package com.airquality.data

import com.airquality.domain.AirCoordinates
import com.airquality.domain.HomeDataModel
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.domain.datasource.ILocationAirQualityDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class LocationAirQualityRepositoryTest {

    private val remoteDataSource = mock<ILocationAirQualityDataSource>()
    private val locationRepository = mock<LocationRepository>()
    private lateinit var repository: LocationAirQualityRepository

    private val testCoordinates = AirCoordinates(41.0, 2.0)
    private val testHomeData = HomeDataModel(city = "Test City")
    private val testHistoricData = HistoricForecastDataModel(city = "Test City")
    private val testForecastData = HistoricForecastDataModel(city = "Test City")

    @Before
    fun setup() {
        repository = LocationAirQualityRepository(remoteDataSource, locationRepository)
    }

    @Test
    fun `getAirQuality emits HomeDataModel`() = runTest {
        whenever(locationRepository.findLastLocation()).thenReturn(testCoordinates)
        whenever(remoteDataSource.getAirQuality(41.0, 2.0)).thenReturn(testHomeData)

        val result = repository.getAirQuality().first()

        assertEquals(testHomeData, result)
    }

    @Test
    fun `getHistoricAirQuality emits HistoricForecastDataModel`() = runTest {
        whenever(locationRepository.findLastLocation()).thenReturn(testCoordinates)
        whenever(remoteDataSource.getHistoricAirQuality(41.0, 2.0)).thenReturn(testHistoricData)

        val result = repository.getHistoricAirQuality().first()

        assertEquals(testHistoricData, result)
    }

    @Test
    fun `getForecastAirQuality emits HistoricForecastDataModel`() = runTest {
        whenever(locationRepository.findLastLocation()).thenReturn(testCoordinates)
        whenever(remoteDataSource.getForecastAirQuality(41.0, 2.0)).thenReturn(testForecastData)

        val result = repository.getForecastAirQuality().first()

        assertEquals(testForecastData, result)
    }
}
