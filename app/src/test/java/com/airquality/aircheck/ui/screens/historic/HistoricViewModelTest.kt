package com.airquality.aircheck.ui.screens.historic

import app.cash.turbine.test
import com.airquality.domain.datasource.AirParameterHistoricForecast
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.usecases.GetHistoricAirQualityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HistoricViewModelTest {

    private val getHistoricAirQualityUseCase: GetHistoricAirQualityUseCase = mock(GetHistoricAirQualityUseCase::class.java)
    private lateinit var viewModel: HistoricViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HistoricViewModel(getHistoricAirQualityUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onUiReady updates historicState with data`() = runTest {
        // Given
        val mockData = HistoricForecastDataModel(
            parameters = listOf(
                AirParameterHistoricForecast(
                    time = "2024-01-01T12:00:00Z",
                    values = mapOf("CO" to 0.5, "PM10" to 20.0)
                )
            )
        )

        whenever(getHistoricAirQualityUseCase.invoke()).thenReturn(flowOf(mockData))

        // When
        viewModel.onUiReady()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.historicState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(mockData, state.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `groupByDay groups forecasts by date`() {
        val list = listOf(
            AirParameterHistoricForecast("2024-01-01T08:00:00Z", emptyMap()),
            AirParameterHistoricForecast("2024-01-01T14:00:00Z", emptyMap()),
            AirParameterHistoricForecast("2024-01-02T10:00:00Z", emptyMap())
        )

        val result = viewModel.groupByDay(list)

        assertEquals(2, result.size)
        assertEquals(2, result["2024-01-01"]?.size)
        assertEquals(1, result["2024-01-02"]?.size)
    }

    @Test
    fun `averageByParameter calculates correct averages`() {
        val list = listOf(
            AirParameterHistoricForecast("2024-01-01T00:00:00Z", mapOf("CO" to 1.0, "PM10" to 20.0)),
            AirParameterHistoricForecast("2024-01-01T01:00:00Z", mapOf("CO" to 3.0, "PM10" to 30.0))
        )

        val result = viewModel.averageByParameter(list)

        assertEquals(2, result.size)
    }

    @Test
    fun `getParameterUnits returns correct units`() {
        assertEquals("mg/m³", viewModel.getParameterUnits("CO"))
        assertEquals("μg/m³", viewModel.getParameterUnits("PM10"))
        assertEquals("μg/m³", viewModel.getParameterUnits("NO2"))
    }
}
