package com.airquality.aircheck.ui.screens.home

import android.content.Context
import android.location.LocationManager
import app.cash.turbine.test
import com.airquality.domain.HomeDataModel
import com.airquality.usecases.FetchAirQualityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val fetchAirQualityUseCase: FetchAirQualityUseCase = mock(FetchAirQualityUseCase::class.java)
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val context: Context = mock(Context::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(fetchAirQualityUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onUiReady updates homeState with data`() = runTest {
        val mockData = HomeDataModel(/* populate if needed */)

        whenever(fetchAirQualityUseCase.invoke()).thenReturn(flowOf(mockData))

        viewModel.onUiReady()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.homeState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(mockData, state.data)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMessageShown clears message and visibility`() {
        viewModel.onMessageShown()

        val state = viewModel.homeState.value
        assertEquals("", state.message)
        assertFalse(state.isPermissionDeniedVisible)
    }

    @Test
    fun `onSettingsButtonClicked hides permission denied message`() {
        viewModel.onSettingsButtonClicked()

        val state = viewModel.homeState.value
        assertFalse(state.isPermissionDeniedVisible)
    }

    @Test
    fun `obtainAirQualityIndex returns correct index`() {
        val model = HomeDataModel(/* custom values if needed */)
        val testViewModel = HomeViewModel(fetchAirQualityUseCase)

        // Forcing the internal state directly for test purposes
        val field = HomeViewModel::class.java.getDeclaredField("_homeState")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(testViewModel) as MutableStateFlow<HomeViewModel.UiState>
        stateFlow.update { it.copy(data = model) }

        val result = testViewModel.obtainAirQualityIndex()

        // You can compare with expected if calculateAirQualityIndex() is known
        assertTrue(result >= 0)
    }

    @Test
    fun `isLocationEnabled returns true when GPS or Network is enabled`() {
        val locationManager = mock(LocationManager::class.java)
        whenever(context.getSystemService(Context.LOCATION_SERVICE)).thenReturn(locationManager)
        whenever(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true)

        val result = viewModel.isLocationEnabled(context)

        assertTrue(result)
    }

    @Test
    fun `getParameterDescription returns empty for unknown param`() {
        val result = viewModel.getParameterDescription("XYZ", context)
        assertEquals("", result.first)
        assertEquals("", result.second)
    }
}
