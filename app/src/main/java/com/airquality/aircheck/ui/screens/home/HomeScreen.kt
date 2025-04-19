package com.airquality.aircheck.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.airquality.aircheck.R
import com.airquality.aircheck.ui.screens.home.utils.PermissionRequestEffect
import com.airquality.aircheck.ui.screens.home.utils.QualityColorBuilders
import com.airquality.domain.AirParameter
import com.airquality.shared.calculateAirParameterValue
import com.airquality.shared.parseDate
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    vm: HomeViewModel = koinViewModel()
) {
    val state by vm.homeState.collectAsState()
    val homeState = rememberHomeState()
    val context = LocalContext.current

    PermissionRequestEffect(permission = Manifest.permission.ACCESS_COARSE_LOCATION) { permission ->
        if (permission) {
            if (vm.isLocationEnabled(context)) {
                vm.onUiReady()
            } else {
                vm.onGPSDisabled(context)
            }
        } else {
            vm.onPermissionDenied(context)
        }
    }

    if (state.message.isNotEmpty()) {
        GPSDisabledDialog(
            showDialog = state.message.isNotEmpty(),
            message = state.message,
            context = context,
            onDismiss = { vm.onMessageShown() }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 32.dp)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Screen(
                        vm = vm,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }

            if (state.isPermissionDeniedVisible) {
                PermissionDeniedCard(
                    onOpenSettings = {
                        vm.onSettingsButtonClicked()
                        homeState.openAppSettings()
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun Screen(
    vm: HomeViewModel,
    modifier: Modifier
) {
    val state by vm.homeState.collectAsState()
    val airQualityIndex = vm.obtainAirQualityIndex()

    var showAQIDialog by remember { mutableStateOf(false) }
    var showFlaticonDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            val qualityColorModel = QualityColorBuilders.getQualityColorModel(airQualityIndex)

            Text(
                text = "${stringResource(R.string.air_quality)} ${stringResource(R.string.aqi_text)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(36.dp))

            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = qualityColorModel.imageColor))
                        .border(6.dp, Color.Gray.copy(alpha = 1f), CircleShape)
                        .shadow(10.dp, shape = CircleShape)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(120.dp)
                        .border(4.dp, colorResource(id = R.color.borderColor), CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    ShowContent(qualityColorModel.image, airQualityIndex)
                }
            }
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAQIDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(R.string.aqi_info_text),
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.data.city,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (state.data.lastUpdated.isNotEmpty()) {
                            parseDate(state.data.lastUpdated)
                        } else {
                            stringResource(R.string.noData_text)
                        },
                        fontSize = 16.sp
                    )
                }

                IconButton(onClick = { showFlaticonDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Receipt,
                        contentDescription = stringResource(R.string.icon_credits_title),
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.parameters),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))

            if (showAQIDialog) {
                AQIInfoDialog(showDialog = showAQIDialog, onDismiss = { showAQIDialog = false })
            }

            if (showFlaticonDialog) {
                FlaticonCreditsDialog(
                    showDialog = showFlaticonDialog,
                    onDismiss = { showFlaticonDialog = false })
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 200.dp,
                        max = 300.dp
                    )
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.data.parameters?.let { parameters ->
                        items(parameters.size) { index ->
                            ParameterCard(parameters[index], vm)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowContent(image: Int, average: Double) {
    var showText by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000L)
            showText = !showText
        }
    }

    when (showText) {
        true -> Text(
            text = String.format("%.0f", average),
            fontSize = 50.sp,
            color = Color.Black
        )

        else -> Icon(
            painter = painterResource(id = image),
            contentDescription = stringResource(R.string.icon_state_description),
            modifier = Modifier
                .size(80.dp),
            colorResource(R.color.black)
        )
    }
}

@Composable
fun ParameterCard(
    parameter: AirParameter,
    vm: HomeViewModel
) {
    var showPopup by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val context = LocalContext.current

    val parameterValue = calculateAirParameterValue(parameter)
    val parameterColor = QualityColorBuilders.getQualityColorModel(parameterValue)
    var showParameterDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tapOffset ->
                        offset = tapOffset
                        showPopup = true
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = BorderStroke(3.dp, colorResource(id = parameterColor.imageColor))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = parameter.parameter,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${parameter.lastValue}"
                )
                Text(
                    text = parameter.units,
                    fontSize = 12.sp
                )
            }
        }
    }

    if (showPopup) {
        Popup(
            alignment = Alignment.TopStart,
            offset = IntOffset(offset.x.toInt(), offset.y.toInt()),
            onDismissRequest = { showPopup = false }
        ) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val parameterDescription =
                        vm.getParameterDescription(parameter.parameter, context)

                    ParameterDialog(
                        showDialog = showParameterDialog,
                        parameterDescription = parameterDescription,
                        parameter = parameter,
                        onDismiss = { showParameterDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionDeniedCard(onOpenSettings: () -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.settings_text)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.open_settings))
            }
        }
    }
}

@Composable
fun AQIInfoDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.aqi_info_text)) },
            text = {
                Column {
                    Text(stringResource(R.string.aqi_colors_title))
                    Spacer(modifier = Modifier.height(8.dp))
                    AQIColorIndicator(color = colorResource(id = R.color.defaultColor), label = stringResource(R.string.noData_text))
                    AQIColorIndicator(color = colorResource(id = R.color.goodColor), label = stringResource(R.string.good_text))
                    AQIColorIndicator(color = colorResource(id = R.color.moderateColor), label = stringResource(R.string.moderate_text))
                    AQIColorIndicator(color = colorResource(id = R.color.unhealthySensibleColor), label = stringResource(R.string.unhealthy_sensible_text))
                    AQIColorIndicator(color = colorResource(id = R.color.unhealthyColor), label = stringResource(R.string.unhealthy_text))
                    AQIColorIndicator(color = colorResource(id = R.color.veryUnhealthyColor), label = stringResource(R.string.very_unhealthy_text))
                    AQIColorIndicator(color = colorResource(id = R.color.hazardousColor), label = stringResource(R.string.hazardous_text))
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.ok_text))
                }
            }
        )
    }
}

@Composable
fun AQIColorIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Canvas(modifier = Modifier.size(16.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
fun ParameterDialog(
    showDialog: Boolean,
    parameterDescription: Pair<String, String>,
    parameter: AirParameter,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(
                text = parameterDescription.first,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ) },
            text = {
                Column {
                    Text(text = "${stringResource(R.string.value_text)} ${parameter.lastValue} ${parameter.units}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = parameterDescription.second, textAlign = TextAlign.Center)
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.ok_text))
                }
            }
        )
    }
}

@Composable
fun FlaticonCreditsDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.icon_credits_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.flaticon_description))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.flaticon_authors))
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.ok_text))
                }
            }
        )
    }
}

@Composable
fun GPSDisabledDialog(showDialog: Boolean, context: Context, message: String, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.warning_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text(stringResource(R.string.open_settings_text))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close_text))
                }
            }
        )
    }
}
