package com.example.stepdiaryandroid.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserRepository(private val context: Context) {

    private val targetStepsKey = longPreferencesKey("target_steps")

    val getTargetSteps: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[targetStepsKey] ?: 10000L
        }

    suspend fun saveTargetSteps(steps: Long) {
        context.dataStore.edit { settings ->
            settings[targetStepsKey] = steps
        }
    }
}