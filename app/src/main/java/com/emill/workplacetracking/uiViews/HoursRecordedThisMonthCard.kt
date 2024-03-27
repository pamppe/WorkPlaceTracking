package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

@Composable
fun HoursRecordedThisMonthCard(weeklyHours: List<Pair<String, Int>>, hourlyRate: Double?) {
    val numberFormat = DecimalFormat.getCurrencyInstance()

    // Calculate total hours for the month
    val totalHoursThisMonth = weeklyHours.sumOf { it.second }
    // Calculate earnings for the month
    val monthlyEarnings = totalHoursThisMonth * (hourlyRate ?: 0.0)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hours Recorded This Month",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            weeklyHours.forEach { week ->
                Text(
                    "${week.second} hours - ${week.first}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Total Hours: $totalHoursThisMonth hours",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "This Month's Earnings: ${numberFormat.format(monthlyEarnings)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}