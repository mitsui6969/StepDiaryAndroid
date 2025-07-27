package com.example.stepdiaryandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stepdiaryandroid.data.HealthConnectRepository
import com.example.stepdiaryandroid.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HomeViewModel(
    private val healthConnectRepository: HealthConnectRepository,
    private val userDataRepository: UserRepository
) : ViewModel() {

    private val _stepCount = MutableStateFlow<Long>(0)
    private val _distance = MutableStateFlow<Double>(0.0)
    private val _calories = MutableStateFlow<Double>(0.0)

    val targetSteps: StateFlow<Long> = userDataRepository.getTargetSteps
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10000L)

    val stepCount: StateFlow<Long> = _stepCount
    val distance: StateFlow<Double> = _distance
    val calories: StateFlow<Double> = _calories

    // 歩数取得
    fun loadSteps(startTime: Instant, endTime: Instant) {
        viewModelScope.launch {
            try {
                val (steps, dist, kcal) = healthConnectRepository.readStepsByTimeRange(startTime, endTime)
                _stepCount.value = steps
                _distance.value = dist
                _calories.value = kcal

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //日時計算
    fun getTodayTimeRange(): Pair<Instant, Instant> {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        val startOfDay = today.atStartOfDay(zoneId).toInstant()
        val endOfDay = today.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant()
        return Pair(startOfDay, endOfDay)
    }

    class Factory(
        private val healthConnectRepository: HealthConnectRepository,
        private val userDataRepository: UserRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(healthConnectRepository, userDataRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
