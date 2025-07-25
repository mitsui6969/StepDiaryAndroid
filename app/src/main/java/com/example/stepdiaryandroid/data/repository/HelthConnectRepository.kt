package com.example.stepdiaryandroid.data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import java.time.Instant
import androidx.health.connect.client.time.TimeRangeFilter
import kotlin.math.round


class HealthConnectRepository(private val healthConnectClient: HealthConnectClient) {

    // 歩数
    suspend fun readStepsByTimeRange(
        startTime: Instant,
        endTime: Instant
    ): Triple<Long, Double, Double> {
        return try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(
                        StepsRecord.COUNT_TOTAL,
                        DistanceRecord.DISTANCE_TOTAL,
                        TotalCaloriesBurnedRecord.ENERGY_TOTAL
                        ),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            val steps = response[StepsRecord.COUNT_TOTAL] ?: 0L
            val distanceRaw = response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
            val caloriesRaw = response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inKilocalories ?: 0.0
            val distance = round(distanceRaw * 10) / 10.0
            val calories = round(caloriesRaw * 10) / 10.0

            Triple(steps, distance, calories)

        } catch (e: Exception) {
            e.printStackTrace()
            Triple(0L, 0.0, 0.0)
        }
    }

}



