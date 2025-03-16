package com.airquality.aircheck.data.datasource.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApiService {

    @GET("air-quality")
    suspend fun fetchAirQuality(
        @Query("latitude")latitude: Double,
        @Query("longitude")longitude: Double,
        @Query("current") current: String = "pm10,pm2_5,carbon_monoxide,ozone,nitrogen_dioxide,sulphur_dioxide",
        ): AirQualityResponseDto
}