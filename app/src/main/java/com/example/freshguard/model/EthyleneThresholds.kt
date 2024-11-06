package com.example.freshguard.model

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


data class EthyleneThresholds(
    val fresh: Float = 50f,
    val ripe: Float = 100f,
    val overripe: Float = 150f,
    val zeroPoint: Float = 0f
)