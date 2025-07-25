package com.example.stepdiaryandroid.data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import java.time.Instant
import androidx.health.connect.client.time.TimeRangeFilter


class HealthConnectRepository(private val healthConnectClient: HealthConnectClient) {

    suspend fun readStepsByTimeRange(
//        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ):Long {
        return try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}



