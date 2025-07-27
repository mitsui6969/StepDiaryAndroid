package com.example.stepdiaryandroid.ui.screen.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.stepdiaryandroid.viewmodel.HomeViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.stepdiaryandroid.ui.theme.ThemeOrenge


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
    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 E曜日", Locale.JAPANESE)
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

        StepsView(stepCount, ThemeOrenge)

        Row {
            Text(text="$distance km")

            Spacer(modifier = Modifier.width(10.dp))

            Text(text="$calories kcal")
        }

        Column {
            Text(text = "目標made: $targetSteps 歩")

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "あと $remainingSteps 歩")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ステップ数の丸
@Composable
fun StepsView(
    steps: Long,
    color: Color,
    modifier: Modifier = Modifier
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(250.dp)
    ){
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 8f
            // drawCircleにstyleを指定することで、塗りつぶしではなく線で描画します
            drawCircle(
                color = color,
                radius = (size.minDimension / 2) - (strokeWidth / 2), // Boxのサイズに追従するように半径を計算
                style = Stroke(width = strokeWidth)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text="$steps",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text="steps",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}

// 目標コンポーす


@Preview(showBackground = true)
@Composable
fun HomeScreenView(){
    StepsView(100, ThemeOrenge)
}
