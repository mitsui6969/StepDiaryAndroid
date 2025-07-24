package com.example.stepdiaryandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stepdiaryandroid.data.HealthConnectRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: HealthConnectRepository
) : ViewModel() {

    fun insertSteps(count: Long = 120L) {
        viewModelScope.launch {
            try{
                repository.insertSteps(count)
            } catch (e:Exception) {
                e.printStackTrace() // ログ確認
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
