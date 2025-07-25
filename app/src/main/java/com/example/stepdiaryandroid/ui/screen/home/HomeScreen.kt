package com.example.stepdiaryandroid.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

        Text(text = "yy/mm/dd")
        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Text(text="1000")
            Text(text="steps")
        }

        Row {
            Text(text="km")
            Text(text="kcal")
        }
    }
}

//@Preview
//@Composable
//fun HomeScreenView(){
//
//}
