package com.example.stepdiaryandroid.ui.screen.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.stepdiaryandroid.viewmodel.SettingViewModel

@Composable
fun SettingScreen(viewModel: SettingViewModel) {
    val targetSteps by viewModel.targetSteps.collectAsState()
    var text by remember(targetSteps) { mutableStateOf(targetSteps.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("目標歩数を設定") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val newTarget = text.toLongOrNull()
            if (newTarget != null) {
                viewModel.saveTargetSteps(newTarget)
            }
        }) {
            Text(text = "保存")
        }
    }
}