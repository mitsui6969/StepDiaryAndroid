package com.example.stepdiaryandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stepdiaryandroid.data.HealthConnectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class HomeViewModel(
    private val repository: HealthConnectRepository
) : ViewModel() {

    private val _stepCount = MutableStateFlow<Long>(0)
    val stepCount: StateFlow<Long> = _stepCount

    fun loadSteps(startTime: Instant, endTime: Instant) {
        viewModelScope.launch {
            try {
                val steps = repository.readStepsByTimeRange(startTime, endTime)
                _stepCount.value = steps
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(
        private val repository: HealthConnectRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
