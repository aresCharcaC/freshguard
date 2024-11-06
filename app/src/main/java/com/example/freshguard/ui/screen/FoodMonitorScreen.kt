package com.example.freshguard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.freshguard.ui.theme.FreshColor
import com.example.freshguard.ui.theme.Primary
import com.example.freshguard.ui.theme.RipeColor
import com.example.freshguard.ui.theme.Secondary
import com.example.freshguard.ui.theme.SpoiledColor
import com.example.freshguard.ui.theme.Tertiary
import com.example.freshguard.ui.theme.VeryRipeColor
import com.example.freshguard.viewmodel.FoodMonitorUiState
import com.example.freshguard.viewmodel.FoodMonitorViewModel

@Composable
fun FoodMonitorScreen(viewModel: FoodMonitorViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(navController)
        Spacer(modifier = Modifier.height(16.dp))
        SensorDataCards(uiState)
        Spacer(modifier = Modifier.height(16.dp))
        StatusCard(uiState)
    }
}

@Composable
private fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "FreshGuard Monitor",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary
        )
        IconButton(onClick = { navController.navigate("config") }) {
            Icon(Icons.Default.Settings, "Configuración")
        }
    }
}

@Composable
private fun StatusCard(uiState: FoodMonitorUiState) {
    val statusColor = when (uiState.foodStatus) {
        "FRESH" -> FreshColor
        "RIPE" -> RipeColor
        "VERY_RIPE" -> VeryRipeColor
        else -> SpoiledColor
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Estado Actual")
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = uiState.foodStatus,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = statusColor
                    )
                }
            }

            HorizontalDivider()

            Text(
                "Prediccion con la ecuación de Arrhenius",
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )
            PredictionRow("Estimated Days Left", "${uiState.scientificDaysLeft} days")

            HorizontalDivider()

            Text(
                "Predicción Basada en Umbrales",
                style = MaterialTheme.typography.titleMedium,
                color = Primary
            )
            PredictionRow("Dias para la siguiente fase", "${uiState.daysToNextPhase} days")
            PredictionRow("Dias hasta deterioro", "${uiState.daysToSpoilage} days")
        }
    }
}

@Composable
private fun PredictionRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Text(
            value,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SensorDataCards(uiState: FoodMonitorUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SensorCard(
            //            average = String.format("Avg: %.1f ppm", uiState.averageEthylene),
            title = "Ethylene",
            value = String.format("Avg: %.1f ppm", uiState.averageEthylene),
            average = String.format("%.1f ppm", uiState.ethylene),
            color = Primary,
            modifier = Modifier.weight(1f)
        )
        SensorCard(
            title = "Temperatura",
            value = String.format("%.1f°C", uiState.temperature),
            color = Secondary,
            modifier = Modifier.weight(1f)
        )
        SensorCard(
            title = "Humedad",
            value = String.format("%.1f%%", uiState.humidity),
            color = Tertiary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SensorCard(
    title: String,
    value: String,
    average: String? = null,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            if (average != null) {
                Text(
                    text = average,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}