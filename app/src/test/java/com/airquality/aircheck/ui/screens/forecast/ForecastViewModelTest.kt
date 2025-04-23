package com.airquality.aircheck.ui.screens.forecast

import app.cash.turbine.test
import com.airquality.domain.datasource.AirParameterHistoricForecast
import com.airquality.domain.datasource.HistoricForecastDataModel
import com.airquality.usecases.GetForecastAirQualityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ForecastViewModelTest {

    private val getForecastAirQualityUseCase: GetForecastAirQualityUseCase =
        mock(GetForecastAirQualityUseCase::class.java)

    private lateinit var viewModel: ForecastViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForecastViewModel(getForecastAirQualityUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onUiReady updates forecastState with data`() = runTest {
        val mockData = HistoricForecastDataModel(
            parameters = listOf(
                AirParameterHistoricForecast(
                    time = "2025-03-25T02:00:00Z",
                    values = mapOf("CO" to 0.8, "PM10" to 15.0)
                )
            )
        )

        whenever(getForecastAirQualityUseCase.invoke()).thenReturn(flowOf(mockData))

        viewModel.onUiReady()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.forecastState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(mockData, state.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `groupByParameter groups values by parameter and hour`() {
        val list = listOf(
            AirParameterHistoricForecast(
                time = "2025-03-25T02:00:00Z",
                values = mapOf("CO" to 0.8, "PM10" to 15.0)
            ),
            AirParameterHistoricForecast(
                time = "2025-03-25T03:00:00Z",
                values = mapOf("CO" to 1.2)
            )
        )

        val result = viewModel.groupByParameter(list)

        assertEquals(2, result["CO"]?.size)
        assertEquals(1, result["PM10"]?.size)
    }

    @Test
    fun `groupByDay groups forecasts by date`() {
        val list = listOf(
            AirParameterHistoricForecast("2025-03-25T02:00:00Z", emptyMap()),
            AirParameterHistoricForecast("2025-03-26T04:00:00Z", emptyMap()),
            AirParameterHistoricForecast("2025-03-25T08:00:00Z", emptyMap())
        )

        val result = viewModel.groupByDay(list)

        assertEquals(2, result.size)
        assertEquals(2, result["2025-03-25"]?.size)
        assertEquals(1, result["2025-03-26"]?.size)
    }

    @Test
    fun `formatToUserDate formats ISO to ddMMyyyy`() {
        val result = viewModel.formatToUserDate("2025-03-25T12:00:00Z")
        assertEquals("25/03/2025", result)
    }

    @Test
    fun `formatToUserDate returns original string if parsing fails`() {
        val badInput = "invalid-date"
        val result = viewModel.formatToUserDate(badInput)
        assertEquals(badInput, result)
    }

    @Test
    fun `getParameterUnits returns correct units`() {
        assertEquals("mg/m³", viewModel.getParameterUnits("CO"))
        assertEquals("μg/m³", viewModel.getParameterUnits("PM10"))
    }
}
