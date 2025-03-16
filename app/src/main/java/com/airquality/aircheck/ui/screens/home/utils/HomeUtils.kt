package com.airquality.aircheck.ui.screens.home.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.FloatRange
import com.airquality.shared.DEFAULT_CITY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume


suspend fun Context.getCity(latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(this)
    val addresses = geocoder.getFromLocationCompat(latitude, longitude, 1)


    val region = addresses.first().subAdminArea
    return region ?: DEFAULT_CITY
}
@Suppress("DEPRECATION")
suspend fun Geocoder.getFromLocationCompat(
    @FloatRange(from = -90.0, to = 90.0) latitude: Double,
    @FloatRange(from = -180.0, to = 180.0) longitude: Double,
    maxResults: Int
): List<Address> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    suspendCancellableCoroutine { continuation ->
        getFromLocation(latitude, longitude, maxResults) {
            continuation.resume(it)
        }
    }
} else {
    withContext(Dispatchers.IO) {
        getFromLocation(latitude, longitude, maxResults) ?: emptyList()
    }
}