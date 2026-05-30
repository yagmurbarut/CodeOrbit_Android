package com.example.codeorbit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue
import kotlinx.coroutines.delay

@Composable
fun QuizResultScreen(
    correctAnswers: Int = 0,
    totalQuestions: Int = 0,
    categoryName: String = "",
    newBadges: List<com.example.codeorbit.network.BadgeResponse> = emptyList(),
    onNewQuiz: () -> Unit = {},
    onBackHome: () -> Unit = {}
) {
    val successRate = if (totalQuestions > 0) (correctAnswers.toFloat() / totalQuestions.toFloat()) else 0f
    val percentage = (successRate * 100).toInt()

    var animationStarted by remember { mutableStateOf(false) }
    var showBadgePopup by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) successRate else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        animationStarted = true
        if (newBadges.isNotEmpty()) {
            delay(800)
            showBadgePopup = true
            delay(4000)
            showBadgePopup = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ana içerik
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        .clip(RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Text("CodeOrbit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = SlateText, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Quiz Completed! 🎉",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                "Harika iş, $categoryName quizini bitirdin!",
                fontSize = 14.sp,
                color = SlateText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Circular Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(192.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(192.dp),
                    color = SlateBackground,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(192.dp),
                    color = PrimaryBlue,
                    strokeWidth = 12.dp,
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$percentage%",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Success Rate", fontSize = 13.sp, color = SlateText)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Doğru/Yanlış özet
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryBlue.copy(alpha = 0.1f))
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(24.dp))
                    Text(
                        "$correctAnswers/$totalQuestions Correct",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Butonlar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNewQuiz,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("New Quiz", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onBackHome,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SlateBackground)
                ) {
                    Text("Back Home", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Badge Pop-up — ekranın üstünde belirir ve kaybolur
        AnimatedVisibility(
            visible = showBadgePopup,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp, start = 16.dp, end = 16.dp),
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                newBadges.forEach { badge ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E3A5F))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(badge.icon ?: "🏆", fontSize = 28.sp)
                        Column {
                            Text(
                                "🎉 Yeni Rozet Kazandın!",
                                fontSize = 11.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                badge.name,
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                badge.description,
                                fontSize = 11.sp,
                                color = SlateText
                            )
                        }
                    }
                }
            }
        }
    }
}