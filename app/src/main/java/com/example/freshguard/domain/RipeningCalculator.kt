package com.example.freshguard.domain

import com.example.freshguard.model.EthyleneThresholds
import com.example.freshguard.model.RipeningData
import com.example.freshguard.model.RipeningPhase
import com.example.freshguard.model.RipeningPrediction
import com.example.freshguard.model.RipeningPredictions
import com.example.freshguard.model.RipeningTrend
import com.example.freshguard.model.ThresholdPrediction
import kotlin.math.exp

data class RipeningPredictions(
    val scientificPrediction: RipeningPrediction,
    val thresholdBasedPrediction: ThresholdPrediction
)

data class ThresholdPrediction(
    val daysToNextPhase: Int,
    val daysToSpoilage: Int
)

class RipeningCalculator {
    companion object {
        private const val BASE_SHELF_LIFE = 15.0
        private const val ACTIVATION_ENERGY = 45000.0
        private const val GAS_CONSTANT = 8.314
        private const val TEMP_OPTIMAL = 293.15
        private const val HUMIDITY_OPTIMAL = 60.0
        private const val ETHYLENE_SENSITIVITY = 0.008
    }

    fun calculateAllPredictions(data: RipeningData): RipeningPredictions {
        return RipeningPredictions(
            scientificPrediction = calculateScientificPrediction(data),
            thresholdBasedPrediction = calculateThresholdPrediction(data)
        )
    }

    private fun calculateScientificPrediction(data: RipeningData): RipeningPrediction {
        val adjustedEthylene = data.ethylene - data.thresholds.zeroPoint

        if (adjustedEthylene <= 0f) {
            return RipeningPrediction(
                daysLeft = 15,
                daysToNextPhase = 7,
                currentPhase = RipeningPhase.FRESH,
                ripeningRate = 0.0,
                ripeningTrend = RipeningTrend.SLOW
            )
        }

        val currentPhase = determinePhase(adjustedEthylene, data.thresholds)
        val tempKelvin = data.temperature + 273.15

        val tempEffect = exp(-ACTIVATION_ENERGY * (1/tempKelvin - 1/TEMP_OPTIMAL) / GAS_CONSTANT)
        val humidityDiff = (data.humidity - HUMIDITY_OPTIMAL) / 50.0
        val humidityEffect = 1.0 / (1.0 + exp(humidityDiff * 2))
        val ethyleneEffect = exp(-ETHYLENE_SENSITIVITY * adjustedEthylene)

        val remainingLife = (BASE_SHELF_LIFE * tempEffect * humidityEffect * ethyleneEffect)
            .toInt()
            .coerceIn(1, 15)

        val ripeningRate = (1.0 / ethyleneEffect).coerceIn(0.1, 2.0)
        val trend = determineRipeningTrend(ripeningRate)

        return RipeningPrediction(
            daysLeft = remainingLife,
            daysToNextPhase = calculateDaysToNextPhase(currentPhase, adjustedEthylene, data.thresholds, tempEffect),
            currentPhase = currentPhase,
            ripeningRate = ripeningRate,
            ripeningTrend = trend
        )
    }

    private fun calculateThresholdPrediction(data: RipeningData): ThresholdPrediction {
        val currentEthylene = data.ethylene - data.thresholds.zeroPoint
        val currentPhase = determinePhase(currentEthylene, data.thresholds)

        val nextThreshold = when (currentPhase) {
            RipeningPhase.FRESH -> data.thresholds.fresh
            RipeningPhase.RIPE -> data.thresholds.ripe
            RipeningPhase.VERY_RIPE -> data.thresholds.overripe
            RipeningPhase.SPOILED -> data.thresholds.overripe
        }

        val ethyleneRate = calculateEthyleneRate(data.historicalReadings)

        val daysToNext = if (ethyleneRate > 0) {
            ((nextThreshold - currentEthylene) / ethyleneRate).toInt().coerceIn(1, 7)
        } else {
            7
        }

        val daysToSpoilage = if (ethyleneRate > 0) {
            ((data.thresholds.overripe - currentEthylene) / ethyleneRate).toInt().coerceIn(1, 15)
        } else {
            15
        }

        return ThresholdPrediction(
            daysToNextPhase = daysToNext,
            daysToSpoilage = daysToSpoilage
        )
    }

    private fun determinePhase(ethylene: Float, thresholds: EthyleneThresholds): RipeningPhase {
        return when {
            ethylene < thresholds.fresh -> RipeningPhase.FRESH
            ethylene < thresholds.ripe -> RipeningPhase.RIPE
            ethylene < thresholds.overripe -> RipeningPhase.VERY_RIPE
            else -> RipeningPhase.SPOILED
        }
    }

    private fun calculateDaysToNextPhase(
        currentPhase: RipeningPhase,
        ethylene: Float,
        thresholds: EthyleneThresholds,
        tempEffect: Double
    ): Int {
        if (currentPhase == RipeningPhase.SPOILED) return 0

        val nextThreshold = when (currentPhase) {
            RipeningPhase.FRESH -> thresholds.fresh
            RipeningPhase.RIPE -> thresholds.ripe
            RipeningPhase.VERY_RIPE -> thresholds.overripe
            RipeningPhase.SPOILED -> return 0
        }

        val ethyleneDiff = nextThreshold - ethylene
        if (ethyleneDiff <= 0) return 1

        return ((ethyleneDiff / (0.15 * tempEffect)) * (1 + ethylene * 0.01))
            .toInt()
            .coerceIn(1, 7)
    }

    private fun determineRipeningTrend(ripeningRate: Double): RipeningTrend {
        return when {
            ripeningRate < 0.8 -> RipeningTrend.SLOW
            ripeningRate < 1.2 -> RipeningTrend.NORMAL
            ripeningRate < 1.5 -> RipeningTrend.FAST
            else -> RipeningTrend.ACCELERATED
        }
    }

    private fun calculateEthyleneRate(historicalReadings: List<Float>): Float {
        if (historicalReadings.size < 2) return 0f
        val recentReadings = historicalReadings.takeLast(10)
        return (recentReadings.last() - recentReadings.first()) / recentReadings.size
    }
}
