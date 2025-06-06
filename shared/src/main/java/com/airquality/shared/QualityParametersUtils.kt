package com.airquality.shared

import com.airquality.domain.AirParameter
import com.airquality.domain.HomeDataModel

fun calculateAirQualityIndex(data: HomeDataModel): Double {
    return data.parameters?.maxOfOrNull { it.lastValue } ?: 0.0
}

fun calculateAirParameterValue(parameter: AirParameter): Double {
    return  when (parameter.parameter) {
            "PM 2.5" -> calculatePM25(parameter.lastValue)
            "PM 10" -> calculatePM10(parameter.lastValue)
            "O₃" -> calculateOzone(parameter.lastValue)
            "NO₂" -> calculateNO2(parameter.lastValue)
            "SO₂" -> calculateSO2(parameter.lastValue)
            "CO" -> calculateCO(parameter.lastValue)
            else -> 0.0
        }
}

private fun calculatePM25(value: Double): Double {
    return when {
        value <= 12.0 -> 0.0
        value <= 35.4 -> 50.0
        value <= 55.4 -> 100.0
        value <= 150.4 -> 150.0
        value <= 250.4 -> 200.0
        value <= 350.4 -> 300.0
        value <= 500.5 -> 400.0
        else -> 500.0
    }
}
private fun calculatePM10(value: Double): Double {
    return when {
        value <= 54 -> 0.0
        value <= 154 -> 50.0
        value <= 254 -> 100.0
        value <= 354 -> 150.0
        value <= 425 -> 200.0
        value <= 604 -> 300.0
        else -> 500.0
    }
}
private fun calculateOzone(value: Double): Double {
    return when {
        value <= 54 -> 0.0
        value <= 70 -> 50.0
        value <= 85 -> 100.0
        value <= 105 -> 150.0
        value <= 200 -> 200.0
        value <= 300 -> 300.0
        value <= 400 -> 400.0
        else -> 500.0
    }
}
private fun calculateNO2(value: Double): Double {
    return when {
        value <= 53 -> 0.0
        value <= 100 -> 50.0
        value <= 360 -> 100.0
        value <= 649 -> 150.0
        value <= 1249 -> 200.0
        value <= 2049 -> 300.0
        else -> 500.0
    }
}
private fun calculateSO2(value: Double): Double {
    return when {
        value <= 35 -> 0.0
        value <= 75 -> 50.0
        value <= 185 -> 100.0
        value <= 304 -> 150.0
        value <= 604 -> 200.0
        value <= 804 -> 300.0
        else -> 500.0
    }
}
private fun calculateCO(value: Double): Double {
    return when {
        value <= 4.4 -> 0.0
        value <= 9.4 -> 50.0
        value <= 12.4 -> 100.0
        value <= 15.4 -> 150.0
        value <= 30.4 -> 200.0
        value <= 40.4 -> 300.0
        else -> 500.0
    }
}
