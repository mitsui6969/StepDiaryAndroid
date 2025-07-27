package com.example.stepdiaryandroid.ui.screen.home

import android.view.Display.Mode
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

        Text(
            text = formattedDate,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(18.dp))

        StepsView(stepCount, ThemeOrenge)

        Spacer(modifier = Modifier.height(10.dp))

        DistCalComponent(distance, calories, ThemeOrenge)

        Spacer(modifier = Modifier.height(20.dp))

        TargetComponent(targetSteps, remainingSteps, ThemeOrenge)
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

// 距離とカロリー
@Composable
fun DistCalComponent(
    distance: Double,
    calories: Double,
    color: Color
){
    Row(
        modifier = Modifier.padding(20.dp)
    ) {
        // 距離
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$distance",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier
                    .padding(end = 4.dp)
            )
            Text(
                text = "km",
                fontSize = 25.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(30.dp))

        // カロリー
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "$calories",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier
                    .padding(end = 4.dp)
            )
            Text(
                text = "kcal",
                fontSize = 25.sp,
                color = Color.Gray
            )
        }
    }
}

// 目標コンポーす
@Composable
fun TargetComponent(
    targetStep: Long,
    remainingSteps: Long,
    color: Color,
    modifier: Modifier = Modifier
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .height(100.dp)
            .fillMaxWidth()
    ){
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = 2f
            val cornerRadius = 16.dp.toPx()
            // drawCircleにstyleを指定することで、塗りつぶしではなく線で描画します
            drawRoundRect(
                color = Color.Gray,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = Size(
                    width = size.width - strokeWidth,
                    height = size.height - strokeWidth
                ),
                cornerRadius = CornerRadius(cornerRadius),
                style = Stroke(width = strokeWidth)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 目標まで
            Column {
                Text(
                    text = "🔥目標まで",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )

                Spacer(modifier)

                Text(
                    text = " 目標: $targetStep",
                    fontSize = 15.sp,
                    color = Color.Gray
                )
            }

            // 残り歩数
            Column(
                horizontalAlignment = Alignment.End
            ){
                Text(
                    text="$remainingSteps",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text="steps",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenView(){
    StepsView(100, ThemeOrenge)
}

@Preview(showBackground = true)
@Composable
fun TargetView() {
    TargetComponent(200, 100, ThemeOrenge)
}

@Preview(showBackground = true)
@Composable
fun DisCalView() {
    DistCalComponent(200.0, 10.0, ThemeOrenge)
}