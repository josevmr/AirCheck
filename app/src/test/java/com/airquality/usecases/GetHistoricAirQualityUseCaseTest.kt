package com.airquality.usecases

import app.cash.turbine.test
import com.airquality.data.LocationAirQualityRepository
import com.airquality.domain.datasource.HistoricForecastDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GetHistoricAirQualityUseCaseTest {

    private lateinit var useCase: GetHistoricAirQualityUseCase
    private val repository: LocationAirQualityRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = GetHistoricAirQualityUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit historic data from repository`() = runTest {
        // Arrange
        val expected = HistoricForecastDataModel(city = "Madrid")
        whenever(repository.getHistoricAirQuality()).thenReturn(flowOf(expected))

        // Act & Assert
        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}
