package com.example.freshguard.util

object Constants {
    // Constantes para límites de días
    const val MIN_DAYS = 1
    const val MAX_DAYS = 15
    const val MIN_NEXT_PHASE_DAYS = 1
    const val MAX_NEXT_PHASE_DAYS = 7

    // Constantes para condiciones óptimas
    const val OPTIMAL_TEMP = 20.0  // Temperatura óptima genérica en °C
    const val OPTIMAL_HUMIDITY = 60.0  // Humedad óptima genérica en %

    // Factores de cálculo
    const val BASE_SHELF_LIFE = 15.0  // Días base de vida útil
    const val TEMP_SENSITIVITY = 0.066  // Sensibilidad a la temperatura
    const val ETHYLENE_SENSITIVITY = 0.008  // Sensibilidad al etileno
    const val HUMIDITY_IMPACT = 0.5  // Impacto de la humedad
    const val ETHYLENE_ACCELERATION = 0.15  // Factor de aceleración por etileno
}
