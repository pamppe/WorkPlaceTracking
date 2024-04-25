package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.emill.workplacetracking.viewmodels.TimerViewModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke



@Composable
fun TimerScreen(timerViewModel: TimerViewModel, navController: NavController) {
    val time by timerViewModel.time.collectAsState()
    val isRunning = timerViewModel.isTimerRunning()

    // Define the progress percentage
    val progress = remember { mutableStateOf(0f) }

    // Update progress as per the timer value
    LaunchedEffect(key1 = time) {
        if (isRunning) {
            // Simulate a progress increment. Calculate your progress based on actual timer seconds elapsed.
            progress.value = (progress.value + 0.01f) % 1f
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(color = Color.White)
    ) {
        Surface(
            modifier = Modifier
                .size(230.dp)
                .padding(15.dp),
            shape = CircleShape,
            color = Color.Transparent
        ) {
            Canvas(modifier = Modifier.size(230.dp)) {
                // Draw the background circle
                drawCircle(
                    color = Color.LightGray,
                    radius = size.minDimension / 2,
                    style = Stroke(width = 15.dp.toPx())
                )
                // Draw the progress arc
                drawArc(
                    color = Color.Blue,
                    startAngle = -90f,
                    sweepAngle = 360 * progress.value,
                    useCenter = false,
                    style = Stroke(width = 15.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.Black),
                    modifier = Modifier.clickable { timerViewModel.toggleTimer() },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
