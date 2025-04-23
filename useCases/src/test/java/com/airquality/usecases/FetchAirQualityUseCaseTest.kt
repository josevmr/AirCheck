package com.airquality.usecases

import app.cash.turbine.test
import com.airquality.data.LocationAirQualityRepository
import com.airquality.domain.HomeDataModel
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
class FetchAirQualityUseCaseTest {

    private lateinit var useCase: FetchAirQualityUseCase

    private val fakeRepository: LocationAirQualityRepository = mock()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = FetchAirQualityUseCase(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should emit HomeDataModel from repository`() = runTest {
        // Arrange
        val expected = HomeDataModel(city = "Test City")
        whenever(fakeRepository.getAirQuality()).thenReturn(flowOf(expected))

        // Act & Assert
        useCase().test {
            assertEquals(expected, awaitItem())
            awaitComplete()
        }
    }
}