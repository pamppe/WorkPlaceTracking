package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emill.workplacetracking.viewmodels.TimerViewModel

@Composable
fun TimerScreen(timerViewModel: TimerViewModel = viewModel()) {
    val time by timerViewModel.time.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // Specify a fixed size for the Surface to ensure it remains circular
        Surface(
            modifier = Modifier
                .size(230.dp) // This makes the Surface have a fixed size
                .padding(15.dp), // Adjust the outer padding if needed
            shape = CircleShape, // Keeps the frame circular
            border = BorderStroke(6.dp, color = Color.LightGray),
            color = Color.Transparent
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                    // No need for padding here since we want the text to be centered
                    // within the Surface, which already has a fixed size
                    modifier = Modifier.clickable { timerViewModel.toggleTimer() },
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}

