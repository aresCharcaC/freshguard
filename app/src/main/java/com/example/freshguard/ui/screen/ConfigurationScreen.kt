package com.example.freshguard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.freshguard.ui.theme.Primary
import com.example.freshguard.viewmodel.FoodMonitorViewModel

@Composable
fun ConfigurationScreen(
    viewModel: FoodMonitorViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var freshInput by remember { mutableStateOf("") }
    var ripeInput by remember { mutableStateOf("") }
    var overripeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopBar(navController)
        Spacer(modifier = Modifier.height(24.dp))

        ThresholdCard(
            currentFresh = uiState.fresh,
            currentRipe = uiState.ripe,
            currentOverripe = uiState.overripe,
            freshInput = freshInput,
            ripeInput = ripeInput,
            overripeInput = overripeInput,
            onFreshChange = { freshInput = it },
            onRipeChange = { ripeInput = it },
            onOverripeChange = { overripeInput = it },
            onSaveThreshold = { type, value ->
                if (value.isEmpty()) return@ThresholdCard

                val floatValue = value.toFloatOrNull()
                if (floatValue == null) {
                    showErrorDialog = true
                    errorMessage = "Ingresa numero valido"
                    return@ThresholdCard
                }

                when (type) {
                    "fresh" -> {
                        if (floatValue >= uiState.ripe) {
                            showErrorDialog = true
                            errorMessage = "El umbral de fresco debe ser menor que el umbral de maduro"
                        } else {
                            viewModel.updateFreshThreshold(floatValue)
                            freshInput = ""
                        }
                    }
                    "ripe" -> {
                        if (floatValue <= uiState.fresh || floatValue >= uiState.overripe) {
                            showErrorDialog = true
                            errorMessage = "El umbral de maduro debe estar entre fresco y pasado"
                        } else {
                            viewModel.updateRipeThreshold(floatValue)
                            ripeInput = ""
                        }
                    }
                    "overripe" -> {
                        if (floatValue <= uiState.ripe) {
                            showErrorDialog = true
                            errorMessage = "El umbral de pasado debe ser mayor que el de maduro"
                        } else {
                            viewModel.updateOverripeThreshold(floatValue)
                            overripeInput = ""
                        }
                    }
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Validation Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun TopBar(navController: NavController) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }
        Text(
            text = "Configure Thresholds",
            style = MaterialTheme.typography.headlineMedium,
            color = Primary
        )
    }
}

@Composable
private fun ThresholdCard(
    currentFresh: Float,
    currentRipe: Float,
    currentOverripe: Float,
    freshInput: String,
    ripeInput: String,
    overripeInput: String,
    onFreshChange: (String) -> Unit,
    onRipeChange: (String) -> Unit,
    onOverripeChange: (String) -> Unit,
    onSaveThreshold: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThresholdField(
                value = freshInput,
                onValueChange = onFreshChange,
                onSave = { onSaveThreshold("fresh", freshInput) },
                label = "Umbral de Frescura (ppm)",
                helper = "Recomendado: 50 ppm\nActual valor: $currentFresh ppm"
            )

            ThresholdField(
                value = ripeInput,
                onValueChange = onRipeChange,
                onSave = { onSaveThreshold("ripe", ripeInput) },
                label = "Umbral de Madurez (ppm)",
                helper = "Recomendado: 100 ppm\nActual valor: $currentRipe ppm"
            )

            ThresholdField(
                value = overripeInput,
                onValueChange = onOverripeChange,
                onSave = { onSaveThreshold("overripe", overripeInput) },
                label = "Umbral para Muy Maduro (ppm)",
                helper = "Recomendado: 150 ppm\nActual valor: $currentOverripe ppm"
            )
        }
    }
}

@Composable
private fun ThresholdField(
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    label: String,
    helper: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onValueChange(newValue)
                }
            },
            label = { Text(label) },
            supportingText = { Text(helper) },
            modifier = Modifier.weight(1f),
            singleLine = true
        )

        Button(
            onClick = onSave,
            enabled = value.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary
            )
        ) {
            Text("Guardar")
        }
    }
}