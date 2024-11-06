package com.example.freshguard.model

data class RipeningPrediction(
    val daysLeft: Int,
    val daysToNextPhase: Int,
    val currentPhase: RipeningPhase,
    val ripeningRate: Double,
    val ripeningTrend: RipeningTrend
)

data class RipeningPredictions(
    val scientificPrediction: RipeningPrediction,
    val thresholdBasedPrediction: ThresholdPrediction
)

data class ThresholdPrediction(
    val daysToNextPhase: Int,
    val daysToSpoilage: Int
)

enum class RipeningPhase {
    FRESH, RIPE, VERY_RIPE, SPOILED
}

enum class RipeningTrend {
    SLOW, NORMAL, FAST, ACCELERATED
}