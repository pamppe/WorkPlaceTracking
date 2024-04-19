package com.emill.workplacetracking.uiViews

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.emill.workplacetracking.R

@Composable
fun StartScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Workplace Tracker", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Image(painter = painterResource(id = R.drawable.kuva), contentDescription = "Start image",
            modifier = Modifier.size(350.dp))

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Hello", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Welcome to Workplace Tracker ")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {navController.navigate("login")},
            modifier = Modifier
                .width(200.dp)
                .height(45.dp), // Customizing height
            shape = RoundedCornerShape(20.dp) // Customizing shape
        ) {
            Text(text = "Existing user")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "or", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(onClick = {navController.navigate("register")},
            modifier = Modifier
                .width(200.dp)
                .height(45.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, Color(0xFF6454a4)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFFffffff), // White background for better contrast
                contentColor = Color(0xFF6454a4)
            )
        ) {
            Text(text = "New user", color = Color(0xFF0000FF), fontWeight = FontWeight.Bold) // Set text color to blue here as well
        }
    }
}