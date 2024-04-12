package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@Composable
fun UserInformationHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${"User"} ${"Test"}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "No company",
            style = MaterialTheme.typography.titleSmall
        )
    }
}