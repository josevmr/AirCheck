package com.airquality.aircheck.data.datasource.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AirQualityResponseDto(
    val latitude: Double,
    val longitude: Double,
    @SerialName("current_units")
    val units: CurrentUnitsDto,
    val current: CurrentDataDto
)

@Serializable
data class CurrentUnitsDto(
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
data class CurrentDataDto(
    val time: String,
    val pm10: Double,
    @SerialName("pm2_5")
    val pm25: Double,
    @SerialName("carbon_monoxide")
    val carbonMonoxide: Double,
    val ozone: Double,
    @SerialName("nitrogen_dioxide")
    val nitrogenDioxide: Double,
    @SerialName("sulphur_dioxide")
    val sulphurDioxide: Double
)

