package com.example.codeorbit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

val SuccessGreen = Color(0xFF0BDA5E)

@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val categoryData = listOf(
        Pair("C#", 0.65f),
        Pair("Java", 0.45f),
        Pair("Py", 0.90f),
        Pair("JS", 0.75f),
        Pair("Swift", 0.30f)
    )

    val missedQuestions = listOf(
        Triple("Memory management in Python...", "Missed 4 times", Color(0xFFEA580C)),
        Triple("Asynchronous JS Event Loop...", "Missed 3 times", Color(0xFFDC2626)),
        Triple("Garbage Collection in Java...", "Missed 2 times", PrimaryBlue)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(SlateBackground),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                Text("Statistics Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = SlateText, modifier = Modifier.size(20.dp))
                }
            }

            // Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Quizzes
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateBackground)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Total Quizzes", fontSize = 13.sp, color = SlateText)
                    Text("152", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Text("+12% this week", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                    }
                }

                // Accuracy
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateBackground)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Accuracy", fontSize = 13.sp, color = SlateText)
                    Text("88%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Text("+3.2% vs avg", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Proficiency Chart
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateBackground)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("Category Proficiency", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Based on last 30 days performance", fontSize = 11.sp, color = SlateText)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("82%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("AVG SCORE", fontSize = 9.sp, color = SlateText, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }

                // Bar Chart
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    categoryData.forEach { (label, value) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .weight(1f),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Arka plan bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.1f))
                                )
                                // Değer barı
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(value)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(PrimaryBlue)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Most Missed Questions
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Most Missed Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    TextButton(onClick = {}) {
                        Text("View All", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                }

                missedQuestions.forEach { (question, missed, iconColor) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SlateBackground)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(question, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
                            Text(missed, fontSize = 11.sp, color = SlateText)
                        }
                        Button(
                            onClick = {},
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Retry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}