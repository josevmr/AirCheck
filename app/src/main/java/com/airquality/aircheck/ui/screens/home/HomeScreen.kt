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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
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
                vm.onGPSDisabled() // ✅ Se usa el mensaje del UiState
            }
        } else {
            vm.onPermissionDenied()
        }
    }

    // Mostrar mensaje si existe
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
        // HEADER

        item {
            val qualityColorModel = QualityColorBuilders.getQualityColorModel(airQualityIndex)

            Text(
                text = "${stringResource(R.string.air_quality)} (AQI)",
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
                // Botón de Información sobre AQI
                IconButton(onClick = { showAQIDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Información AQI",
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
                            "No data"
                        },
                        fontSize = 16.sp
                    )
                }

                // Botón de Créditos (Flaticon)
                IconButton(onClick = { showFlaticonDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info, // Puedes usar otro icono si quieres
                        contentDescription = "Créditos de iconos",
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

        // PARAMETERS

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(
                        min = 200.dp,
                        max = 300.dp
                    ) // Establece un tamaño razonable para el grid
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(100.dp),
                    modifier = Modifier.fillMaxSize(), // Asegura que ocupe el espacio del Box
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
            contentDescription = "icon",
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
                    Text(
                        text = parameterDescription.first,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${stringResource(R.string.value_text)} ${parameter.lastValue} ${parameter.units}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = parameterDescription.second, textAlign = TextAlign.Center)
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
            title = { Text("Información sobre AQI") },
            text = {
                Column {
                    Text("El Índice de Calidad del Aire (AQI) sigue la escala de la EPA de EE.UU. Se basa en los valores máximos de los contaminantes y se representa con los siguientes colores:")
                    Spacer(modifier = Modifier.height(8.dp))
                    AQIColorIndicator(color = colorResource(id = R.color.defaultColor), label = "Sin datos")
                    AQIColorIndicator(color = colorResource(id = R.color.goodColor), label = "0-50: Bueno")
                    AQIColorIndicator(color = colorResource(id = R.color.moderateColor), label = "51-100: Moderado")
                    AQIColorIndicator(color = colorResource(id = R.color.unhealthySensibleColor), label = "101-150: No saludable para grupos sensibles") // Naranja
                    AQIColorIndicator(color = colorResource(id = R.color.unhealthyColor), label = "151-200: No saludable")
                    AQIColorIndicator(color = colorResource(id = R.color.veryUnhealthyColor), label = "201-300: Muy no saludable") // Púrpura
                    AQIColorIndicator(color = colorResource(id = R.color.hazardousColor), label = "300+: Peligroso") // Granate
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Entendido")
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
fun FlaticonCreditsDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Créditos de Iconos") },
            text = {
                Column {
                    Text("Los iconos usados en esta aplicación son de Flaticon. Se debe mencionar su uso si se publica la aplicación.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📌 Flaticon: www.flaticon.com")
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
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
            title = { Text("Aviso") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    context.startActivity(intent)
                }) {
                    Text("Abrir Ajustes")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        )
    }
}
