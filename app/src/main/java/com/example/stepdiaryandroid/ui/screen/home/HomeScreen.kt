package com.example.stepdiaryandroid.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stepdiaryandroid.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    val stepCount by viewModel.stepCount.collectAsState()
    val distance by viewModel.distance.collectAsState(initial = 0.0)
    val calories by viewModel.calories.collectAsState(initial = 0.0)
    val targetSteps by viewModel.targetSteps.collectAsState()
    val remainingSteps = (targetSteps - stepCount).coerceAtLeast(0)

    // 今日の日付を取得してフォーマット
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日")
    val formattedDate = today.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = formattedDate)
        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(text="$stepCount")
            Text(text="steps")
        }

        Row {
            Text(text="$distance km")

            Spacer(modifier = Modifier.width(10.dp))

            Text(text="$calories kcal")
        }

        Column {
            Text(text = "目標: $targetSteps 歩", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "あと $remainingSteps 歩", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun HomeScreenView(){

}
