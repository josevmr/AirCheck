package com.airquality.aircheck.ui.screens.historic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airquality.aircheck.R
import com.airquality.aircheck.ui.screens.home.utils.QualityColorBuilders
import com.airquality.domain.datasource.AirParameterHistoricForecast
import com.airquality.shared.formattedDate
import org.koin.androidx.compose.koinViewModel


@Composable
fun HistoricScreen(
    vm: HistoricViewModel = koinViewModel()
) {
    val state by vm.historicState.collectAsState()
    val groupedDays = remember(state.data.parameters) {
        vm.groupByDay(state.data.parameters ?: emptyList())
    }

    LaunchedEffect(Unit) {
        vm.onUiReady()
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isCompact = maxWidth < 600.dp

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = if (isCompact) 16.dp else 32.dp)
                        .widthIn(max = 600.dp)
                        .padding(top = 32.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.previous_days_text)} - ${state.data.city}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 116.dp)
                    ) {
                        groupedDays
                            .toSortedMap(compareByDescending { it })
                            .forEach { (day, values) ->
                                item {
                                    DayCard(day = day, items = values, vm = vm)
                                }
                            }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayCard(
    day: String,
    items: List<AirParameterHistoricForecast>,
    vm: HistoricViewModel = koinViewModel()
) {
    val dailyValues = items.flatMap { it.values.values }
    val dailyAverage = items
        .map { dailyValues.maxOfOrNull { value -> value } ?: 0.0 }
        .average()
    val colorModel = QualityColorBuilders.getQualityColorModel(dailyAverage)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = formattedDate(day),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "${stringResource(R.string.aqi_average_text)} ${dailyAverage.toInt()}",
                        color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(colorResource(id = colorModel.imageColor))
                ) {
                    Icon(
                        painter = painterResource(id = colorModel.image),
                        contentDescription = stringResource(R.string.icon_state_description),
                        modifier = Modifier
                            .size(48.dp),
                        colorResource(R.color.black)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                val paramAvg = vm.averageByParameter(items)

                paramAvg.forEach { (param, value) ->
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color.Black),
                        tonalElevation = 4.dp,
                        shadowElevation = 2.dp,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(
                            text = "$param: ${"%.1f".format(value)} ${vm.getParameterUnits(param)}",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}