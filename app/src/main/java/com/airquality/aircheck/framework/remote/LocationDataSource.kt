package com.airquality.aircheck.framework.remote

import android.annotation.SuppressLint
import com.airquality.domain.AirCoordinates
import com.airquality.domain.datasource.ILocationDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationDataSource(
    private val fusedLocationClient: FusedLocationProviderClient
) : ILocationDataSource {

    override suspend fun findLastLocation(): AirCoordinates? {
        val location = fusedLocationClient.lastLocation()
        return location?.let {
            AirCoordinates(
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun FusedLocationProviderClient.lastLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location.toDomainLocation())
                } else {
                    continuation.resume(Location(41.3870, 2.1701))
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    private fun android.location.Location.toDomainLocation(): Location = Location(latitude, longitude)
}

data class Location(
    val latitude: Double,
    val longitude: Double
)