package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emill.workplacetracking.db.WorkEntry
import com.emill.workplacetracking.viewmodels.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun UserProfileScreen(viewModel: MainViewModel) {
    val userInfo by viewModel.userInfo.observeAsState()
    val userId = userInfo?.id
    val hourlyRate = userInfo?.hourlyRate ?: 0.0

    val weeklyEntries by viewModel.getWorkEntriesForCurrentWeek(userId ?: -1).observeAsState(initial = emptyList())
    val weeklyHours = weeklyEntries.sumOf { it.hoursWorked }
    val weeklyEarnings = weeklyHours * hourlyRate

    val currentMonthEntries by viewModel.getWorkEntriesForCurrentMonth(userId ?: -1).observeAsState(initial = emptyList())
    val totalHoursAllTime by viewModel.getTotalHoursAllTime(userId ?: -1).observeAsState(initial = 0)

    val weeklyHoursTransformed = transformEntriesToWeeklyHours(currentMonthEntries)

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserInformationHeader(userInfo = userInfo)

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        HoursRecordedThisWeekCard(weeklyHours = weeklyHours, weeklyEarnings = weeklyEarnings)
        Spacer(modifier = Modifier.height(8.dp))

        HoursRecordedThisMonthCard(weeklyHours = weeklyHoursTransformed, hourlyRate = hourlyRate)
        Spacer(modifier = Modifier.height(8.dp))

        TotalHoursRecordedCard(totalHours = totalHoursAllTime, hourlyRate = hourlyRate)
    }
}

fun transformEntriesToWeeklyHours(entries: List<WorkEntry>): List<Pair<String, Int>> {
    val weekFields = WeekFields.of(Locale.getDefault())

    // Group entries by week of the month
    val entriesByWeek = entries.groupBy {
        val date = LocalDate.parse(it.date, DateTimeFormatter.ISO_DATE)
        date.get(weekFields.weekOfMonth())
    }

    // Sum hours for each week and map to a list of pairs
    return entriesByWeek.map { (weekOfMonth, entries) ->
        "Week $weekOfMonth" to entries.sumOf { it.hoursWorked }
    }.sortedBy { it.first } // Ensure the weeks are in order
}