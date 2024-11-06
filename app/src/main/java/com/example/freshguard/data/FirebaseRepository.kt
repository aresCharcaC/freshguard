package com.example.freshguard.data

import com.example.freshguard.model.EthyleneThresholds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val sensorDataRef = database.getReference("sensor_data/latest")
    private val thresholdsRef = database.getReference("thresholds")

    private val _lastReadings = MutableStateFlow<List<Float>>(emptyList())
    val lastReadings: StateFlow<List<Float>> = _lastReadings.asStateFlow()

    val sensorData = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ethylene = snapshot.child("ethylene_ppm").getValue(Float::class.java) ?: 0f
                val temperature = snapshot.child("temperature").getValue(Float::class.java) ?: 0f
                val humidity = snapshot.child("humidity").getValue(Float::class.java) ?: 0f
                val ethyleneHistory = snapshot.child("ethylene_history")
                    .children.mapNotNull { it.getValue(Float::class.java) }

                _lastReadings.value = ethyleneHistory
                trySend(SensorData(ethylene, temperature, humidity))
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }

        sensorDataRef.addValueEventListener(listener)
        awaitClose { sensorDataRef.removeEventListener(listener) }
    }

    suspend fun getThresholds(): EthyleneThresholds {
        val snapshot = thresholdsRef.get().await()
        return snapshot.getValue(EthyleneThresholds::class.java) ?: EthyleneThresholds()
    }

    suspend fun saveThresholds(thresholds: EthyleneThresholds) {
        thresholdsRef.setValue(thresholds).await()
    }
}

data class SensorData(
    val ethylene: Float,
    val temperature: Float,
    val humidity: Float
)