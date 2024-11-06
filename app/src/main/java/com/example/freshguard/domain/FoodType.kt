package com.example.freshguard.domain

sealed class FoodType(
    val name: String,
    val baseRipeningRate: Double,
    val temperatureSensitivity: Double,
    val ethyleneSensitivity: Double,
    val optimalTemp: Double,
    val optimalHumidity: Double
) {
    object BANANA : FoodType("Banana", 1.2, 0.07, 0.009, 13.0, 90.0)
    object APPLE : FoodType("Apple", 1.0, 0.06, 0.008, 4.0, 90.0)
    object AVOCADO : FoodType("Avocado", 1.4, 0.08, 0.01, 7.0, 85.0)
    object TOMATO : FoodType("Tomato", 1.1, 0.065, 0.008, 10.0, 85.0)
    object GENERIC : FoodType("Generic", 1.0, 0.066, 0.008, 10.0, 85.0)
}

data class SensorReading(
    val ethylene: Float,
    val temperature: Float,
    val humidity: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class RipeningState(
    val currentState: FoodState,
    val daysLeft: Int,
    val daysToNextPhase: Int,
    val ripeningRate: Double,
    val ethyleneLevel: Float,
    val recommendation: String
)

enum class FoodState {
    FRESH,
    RIPE,
    VERY_RIPE,
    SPOILED
}

data class ThresholdConfig(
    val fresh: Float,
    val ripe: Float,
    val overRipe: Float,
    val zeroPoint: Float = 0f
) {
    fun validate(): Boolean {
        return fresh < ripe &&
                ripe < overRipe &&
                fresh > 0 &&
                ripe > 0 &&
                overRipe > 0
    }
}

sealed class RipeningResult<out T> {
    data class Success<T>(val data: T) : RipeningResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : RipeningResult<Nothing>()
    object Loading : RipeningResult<Nothing>()
}