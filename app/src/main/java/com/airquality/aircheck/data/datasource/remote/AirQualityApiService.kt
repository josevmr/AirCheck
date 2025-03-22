package com.airquality.aircheck.data.datasource.remote

import com.airquality.shared.API_VALUES
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApiService {

    @GET("air-quality")
    suspend fun fetchAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = API_VALUES,
    ): AirQualityResponseDto

    @GET("air-quality")
    suspend fun getHistoryQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("past_days") pastDays: Int = 7,
        @Query("forecast_days") forecastDays: Int = 0,
        @Query("hourly") current: String = API_VALUES,
    ): AirHistoricForecastResponseDto

    @GET("air-quality")
    suspend fun getForecastQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("hourly") current: String = API_VALUES,
    ): AirHistoricForecastResponseDto
}