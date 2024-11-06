package com.example.freshguard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshguard.data.FirebaseRepository
import com.example.freshguard.domain.RipeningCalculator
import com.example.freshguard.model.EthyleneThresholds
import com.example.freshguard.model.RipeningData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoodMonitorViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    private val calculator = RipeningCalculator()
    private var thresholds = EthyleneThresholds()

    private val _uiState = MutableStateFlow(FoodMonitorUiState())
    val uiState: StateFlow<FoodMonitorUiState> = _uiState.asStateFlow()

    init {
        observeSensorData()
        loadThresholds()
    }

    private fun observeSensorData() {
        viewModelScope.launch {
            repository.sensorData.collect { sensorData ->
                val readings = repository.lastReadings.value
                val avgEthylene = if (readings.isNotEmpty()) {
                    readings.average().toFloat() - thresholds.zeroPoint
                } else {
                    sensorData.ethylene - thresholds.zeroPoint
                }

                updateFoodStatus(
                    avgEthylene.coerceAtLeast(0f),
                    sensorData.ethylene - thresholds.zeroPoint,
                    sensorData.temperature,
                    sensorData.humidity
                )
            }
        }
    }

    private fun updateFoodStatus(
        avgEthylene: Float,
        currentEthylene: Float,
        temperature: Float,
        humidity: Float
    ) {
        val predictions = calculator.calculateAllPredictions(
            RipeningData(
                ethylene = currentEthylene,
                temperature = temperature,
                humidity = humidity,
                historicalReadings = repository.lastReadings.value,
                thresholds = thresholds
            )
        )

        viewModelScope.launch {
            _uiState.emit(
                _uiState.value.copy(
                    averageEthylene = avgEthylene,
                    ethylene = currentEthylene,
                    temperature = temperature,
                    humidity = humidity,
                    foodStatus = predictions.scientificPrediction.currentPhase.name,
                    scientificDaysLeft = predictions.scientificPrediction.daysLeft,
                    daysToNextPhase = predictions.thresholdBasedPrediction.daysToNextPhase,
                    daysToSpoilage = predictions.thresholdBasedPrediction.daysToSpoilage
                )
            )
        }
    }

    fun updateFreshThreshold(fresh: Float) {
        viewModelScope.launch {
            if (fresh > 0 && fresh < thresholds.ripe) {
                val updatedThresholds = thresholds.copy(fresh = fresh)
                thresholds = updatedThresholds
                repository.saveThresholds(thresholds)
                _uiState.emit(_uiState.value.copy(fresh = fresh))
                updateFoodStatus(
                    _uiState.value.averageEthylene,
                    _uiState.value.ethylene,
                    _uiState.value.temperature,
                    _uiState.value.humidity
                )
            }
        }
    }

    fun updateRipeThreshold(ripe: Float) {
        viewModelScope.launch {
            if (ripe > thresholds.fresh && ripe < thresholds.overripe) {
                val updatedThresholds = thresholds.copy(ripe = ripe)
                thresholds = updatedThresholds
                repository.saveThresholds(thresholds)
                _uiState.emit(_uiState.value.copy(ripe = ripe))
                updateFoodStatus(
                    _uiState.value.averageEthylene,
                    _uiState.value.ethylene,
                    _uiState.value.temperature,
                    _uiState.value.humidity
                )
            }
        }
    }

    fun updateOverripeThreshold(overripe: Float) {
        viewModelScope.launch {
            if (overripe > thresholds.ripe) {
                val updatedThresholds = thresholds.copy(overripe = overripe)
                thresholds = updatedThresholds
                repository.saveThresholds(thresholds)
                _uiState.emit(_uiState.value.copy(overripe = overripe))
                updateFoodStatus(
                    _uiState.value.averageEthylene,
                    _uiState.value.ethylene,
                    _uiState.value.temperature,
                    _uiState.value.humidity
                )
            }
        }
    }

    private fun loadThresholds() {
        viewModelScope.launch {
            thresholds = repository.getThresholds()
            _uiState.emit(_uiState.value.copy(
                fresh = thresholds.fresh,
                ripe = thresholds.ripe,
                overripe = thresholds.overripe
            ))
        }
    }
}

data class FoodMonitorUiState(
    val averageEthylene: Float = 0f,
    val ethylene: Float = 0f,
    val temperature: Float = 0f,
    val humidity: Float = 0f,
    val foodStatus: String = "",
    val scientificDaysLeft: Int = 15,
    val daysToNextPhase: Int = 7,
    val daysToSpoilage: Int = 15,
    val fresh: Float = 50f,
    val ripe: Float = 100f,
    val overripe: Float = 150f
)
