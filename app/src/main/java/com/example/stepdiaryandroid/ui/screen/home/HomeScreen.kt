package com.example.stepdiaryandroid.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import com.example.stepdiaryandroid.data.HealthConnectRepository
import com.example.stepdiaryandroid.ui.theme.StepDiaryAndroidTheme
import com.example.stepdiaryandroid.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Step Diary")
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.insertSteps(100L) // 任意のステップ数
        }) {
            Text("ステップを記録する")
        }
    }
}


