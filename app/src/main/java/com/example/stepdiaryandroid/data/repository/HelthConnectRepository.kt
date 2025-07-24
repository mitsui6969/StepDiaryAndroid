package com.example.stepdiaryandroid.data

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

class HealthConnectRepository(private val healthConnectClient: HealthConnectClient) {

    suspend fun insertSteps(count: Long = 120L) {  // countはLong型
        val endTime = Instant.now()
        val startTime = endTime.minus(Duration.ofMinutes(15))

        val stepsRecord = StepsRecord(
            count = 120,
            startTime = startTime,
            endTime = endTime,
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC,
            metadata = Metadata.autoRecorded(
                device = Device(type = Device.TYPE_WATCH)
            )
        )

        try {
            healthConnectClient.insertRecords(listOf(stepsRecord))
        } catch (e: Exception) {
            e.printStackTrace() // 適宜エラー処理
        }
    }
}


