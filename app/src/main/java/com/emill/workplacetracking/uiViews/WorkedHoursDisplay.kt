package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emill.workplacetracking.viewmodels.MainViewModel

@Composable
fun WorkedHoursDisplay(mainViewModel: MainViewModel, userId: Int) {
    // Apply Modifier.weight(1f) to fill remaining space and Modifier.fillMaxWidth() to fill width
    Box(modifier = Modifier
        .fillMaxWidth() // Fill the width of its parent
        // Use weight to make it fill the remaining space
        .fillMaxHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Tehdyt tunnit", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = Color.White)
            // Implementation for displaying work entries
            WorkEntriesDisplay(mainViewModel = mainViewModel, userId = userId)
        }
    }
}