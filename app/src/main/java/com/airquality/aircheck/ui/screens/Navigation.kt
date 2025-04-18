package com.airquality.aircheck.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.airquality.aircheck.R
import com.airquality.aircheck.ui.screens.forecast.ForecastScreen
import com.airquality.aircheck.ui.screens.historic.HistoricScreen
import com.airquality.aircheck.ui.screens.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Historic

@Serializable
object Forecast


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen()
        }
        composable<Historic> {
            HistoricScreen()
        }
        composable<Forecast> {
            ForecastScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Historic, Icons.Default.DateRange, stringResource(R.string.previous_days_text)),
        BottomNavItem(Home, Icons.Default.Home, stringResource(R.string.home_text)),
        BottomNavItem(Forecast, Icons.Default.Favorite, stringResource(R.string.forecast_text))
    )

    NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets(0, 0, 0, 0))
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route::class.qualifiedName,
                onClick = {
                    navController.navigate(item.route::class.qualifiedName!!) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem<T : Any>(
    val route: T,
    val icon: ImageVector,
    val label: String
)