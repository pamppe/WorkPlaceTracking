package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emill.workplacetracking.viewmodels.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WorkEntriesDisplay(mainViewModel: MainViewModel, userId: Int) {
    val workEntries by mainViewModel.getAggregatedWorkEntriesForUser(userId).observeAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        if (workEntries.isEmpty()) {
            Text(
                text = "No work entries available for this week yet.",
                fontSize = 20.sp,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically) // Center vertically within the Box
                    .padding(horizontal = 16.dp), // Adjust padding to ensure content is centered
                horizontalAlignment = Alignment.CenterHorizontally // Center items horizontally
            ) {
                workEntries.forEach { entry ->
                    val formattedDate = formatDate(entry.date)
                    val (weekday, date) = formattedDate.split(", ").let { it[0] to it[1] }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(), // Fill the width and center content
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            color = Color.Black,
                            text = weekday,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            color = Color.Black,
                            text = date,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            color = Color.Black,
                            text = "${entry.totalHoursWorked}h",
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

fun formatDate(dateStr: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val outputFormatter = DateTimeFormatter.ofPattern("EEEE, dd/MM", Locale.getDefault())
    val date = LocalDate.parse(dateStr, inputFormatter)
    return date.format(outputFormatter)
}
