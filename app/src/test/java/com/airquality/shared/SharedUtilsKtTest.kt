package com.airquality.shared

import com.airquality.domain.AirParameter
import com.airquality.domain.HomeDataModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedUtilsTest {

    @Test
    fun `parseDate should convert ISO date to readable format`() {
        val input = "2025-04-17T15:00"
        val expected = "17/04/2025 15:00"
        assertEquals(expected, parseDate(input))
    }

    @Test
    fun `formattedDate should convert valid date string`() {
        val input = "2025-04-17"
        val expected = "17/04/2025"
        assertEquals(expected, formattedDate(input))
    }

    @Test
    fun `formattedDate should return input if parsing fails`() {
        val input = "invalid-date"
        assertEquals(input, formattedDate(input))
    }

    @Test
    fun `calculateAirQualityIndex returns max parameter value`() {
        val model = HomeDataModel(
            parameters = listOf(
                AirParameter("PM 2.5", 23.5, "μg/m³"),
                AirParameter("CO", 80.0, "mg/m³")
            )
        )
        assertEquals(80.0, calculateAirQualityIndex(model), 0.01)
    }

    @Test
    fun `calculateAirParameterValue returns correct AQI for PM 10`() {
        val param = AirParameter("PM 10", 200.0, "μg/m³")
        val result = calculateAirParameterValue(param)
        assertEquals(100.0, result, 0.01)
    }

    @Test
    fun `calculateAirParameterValue returns zero for unknown parameter`() {
        val param = AirParameter("UNKNOWN", 10.0, "μg/m³")
        assertEquals(0.0, calculateAirParameterValue(param), 0.01)
    }
}
