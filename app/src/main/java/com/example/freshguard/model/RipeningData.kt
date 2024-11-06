package com.example.freshguard.model

data class RipeningData(
    val ethylene: Float,
    val temperature: Float,
    val humidity: Float,
    val historicalReadings: List<Float>,
    val thresholds: EthyleneThresholds
)