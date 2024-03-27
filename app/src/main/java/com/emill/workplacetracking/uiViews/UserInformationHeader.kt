package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.emill.workplacetracking.db.UserInfo

@Composable
fun UserInformationHeader(userInfo: UserInfo?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${userInfo?.firstName ?: "User"} ${userInfo?.lastName ?: ""}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = userInfo?.company ?: "No company",
            style = MaterialTheme.typography.titleSmall
        )
    }
}