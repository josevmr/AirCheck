package com.airquality.aircheck.data.datasource.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AirHistoricForecastResponseDto(
    val latitude: Double,
    val longitude: Double,
    @SerialName("generationtime_ms")
    val generationTime: Double,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerialName("hourly_units")
    val hourlyUnits: CurrentHistoricForecastUnitsDto,
    val hourly: CurrentHistoricForecastDataDto
)

@Serializable
data class CurrentHistoricForecastUnitsDto(
    val time: String,
    val pm10: String,
    @SerialName("pm2_5")
    val pm25: String,
    @SerialName("carbon_monoxide")
    val carbonMonoxide: String,
    val ozone: String,
    @SerialName("nitrogen_dioxide")
    val nitrogenDioxide: String,
    @SerialName("sulphur_dioxide")
    val sulphurDioxide: String
)

@Serializable
data class CurrentHistoricForecastDataDto(
    val time: List<String>,
    val pm10: List<Double>,
    @SerialName("pm2_5")
    val pm25: List<Double>,
    @SerialName("carbon_monoxide")
    val carbonMonoxide: List<Double>,
    val ozone: List<Double>,
    @SerialName("nitrogen_dioxide")
    val nitrogenDioxide: List<Double>,
    @SerialName("sulphur_dioxide")
    val sulphurDioxide: List<Double>
)