package com.example.stepdiaryandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stepdiaryandroid.data.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(private val userDataRepository: UserRepository) : ViewModel() {

    val targetSteps: StateFlow<Long> = userDataRepository.getTargetSteps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 10000L
        )

    fun saveTargetSteps(steps: Long) {
        viewModelScope.launch {
            userDataRepository.saveTargetSteps(steps)
        }
    }

    class Factory(private val userDataRepository: UserRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingViewModel(userDataRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}