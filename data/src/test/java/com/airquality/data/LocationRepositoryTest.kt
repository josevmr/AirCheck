package com.airquality.data

import com.airquality.domain.AirCoordinates
import com.airquality.domain.datasource.ILocationDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class LocationRepositoryTest {

    private val locationDataSource = mock<ILocationDataSource>()
    private lateinit var repository: LocationRepository

    @Before
    fun setup() {
        repository = LocationRepository(locationDataSource)
    }

    @Test
    fun `findLastLocation returns location from data source`() = runTest {
        val expected = AirCoordinates(10.0, 20.0)
        whenever(locationDataSource.findLastLocation()).thenReturn(expected)

        val result = repository.findLastLocation()

        assertEquals(expected, result)
    }
}