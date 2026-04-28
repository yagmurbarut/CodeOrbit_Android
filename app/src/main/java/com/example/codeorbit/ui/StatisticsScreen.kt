package com.example.codeorbit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

val SuccessGreen = Color(0xFF0BDA5E)

@Composable
fun StatisticsScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.statistics
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Genel", "Kategoriler", "Hatalar")

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadStatistics(effectiveUserId)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(BackgroundDark)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryBlue)
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(50)).background(SlateBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                    Text("Statistics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Box(modifier = Modifier.size(40.dp))
                }

                // Tabs
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    tab,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == index) PrimaryBlue else SlateText
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(if (selectedTab == index) PrimaryBlue else Color.Transparent)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = SlateBorder)

                // Tab içerikleri
                when (selectedTab) {
                    0 -> GenelTab(stats = stats)
                    1 -> KategorilerTab(stats = stats)
                    2 -> HatalarTab(stats = stats)
                }
            }
        }
    }
}

// ========== GENEL TAB ==========
@Composable
fun GenelTab(stats: com.example.codeorbit.network.StatisticsResponse?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Total Quizzes & Accuracy
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Total Quizzes", fontSize = 13.sp, color = SlateText)
                Text("${stats?.totalQuizzes ?: 0}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Text("Tüm zamanlar", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }
            }
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Accuracy", fontSize = 13.sp, color = SlateText)
                Text("${"%.0f".format(stats?.overallSuccessRate ?: 0.0)}%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                    Text("Ortalama skor", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Correct / Wrong
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Correct", fontSize = 13.sp, color = SlateText)
                Text("${stats?.totalCorrectAnswers ?: 0}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                Text("/ ${stats?.totalQuestionsSolved ?: 0} soru", fontSize = 11.sp, color = SlateText)
            }
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Wrong", fontSize = 13.sp, color = SlateText)
                Text("${stats?.totalWrongAnswers ?: 0}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Red.copy(alpha = 0.8f))
                Text("/ ${stats?.totalQuestionsSolved ?: 0} soru", fontSize = 11.sp, color = SlateText)
            }
        }

        // Streak
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Current Streak", fontSize = 13.sp, color = SlateText)
                Text("${stats?.currentStreak ?: 0} 🔥", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("gün", fontSize = 11.sp, color = SlateText)
            }
            Column(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Longest Streak", fontSize = 13.sp, color = SlateText)
                Text("${stats?.longestStreak ?: 0} 🏆", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("en uzun", fontSize = 11.sp, color = SlateText)
            }
        }

        // Difficulty breakdown
        if (!stats?.difficultyStats.isNullOrEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Zorluk Seviyesi", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                stats?.difficultyStats?.forEach { diff ->
                    val value = (diff.successRate / 100).toFloat().coerceIn(0f, 1f)
                    val color = when (diff.difficultyLevel) {
                        "Easy" -> SuccessGreen
                        "Medium" -> Color(0xFFFBBF24)
                        "Hard" -> Color(0xFFEF4444)
                        else -> PrimaryBlue
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(diff.difficultyLevel, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${"%.0f".format(diff.successRate)}% (${diff.correctAnswers}/${diff.questionsSolved})",
                                fontSize = 13.sp, color = color, fontWeight = FontWeight.Bold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { value },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = color,
                            trackColor = SlateBorder
                        )
                    }
                }
            }
        }
    }
}

// ========== KATEGORİLER TAB ==========
@Composable
fun KategorilerTab(stats: com.example.codeorbit.network.StatisticsResponse?) {
    if (stats?.categoryStats.isNullOrEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Henüz kategori verisi yok", fontSize = 14.sp, color = SlateText)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bar Chart
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text("Kategori Başarısı", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Kategoriye göre performans", fontSize = 11.sp, color = SlateText)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${"%.0f".format(stats?.overallSuccessRate ?: 0.0)}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("GENEL", fontSize = 9.sp, color = SlateText, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                stats?.categoryStats?.forEach { cat ->
                    val value = (cat.successRate / 100).toFloat().coerceIn(0f, 1f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text("${"%.0f".format(cat.successRate)}%", fontSize = 9.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.width(28.dp).weight(1f), contentAlignment = Alignment.BottomCenter) {
                            Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).background(PrimaryBlue.copy(alpha = 0.1f)))
                            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(value).clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).background(PrimaryBlue))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(cat.categoryName.take(4), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateText)
                    }
                }
            }
        }

        // Her kategori detayı
        stats?.categoryStats?.forEach { cat ->
            val value = (cat.successRate / 100).toFloat().coerceIn(0f, 1f)
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(cat.categoryName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${"%.0f".format(cat.successRate)}%", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
                LinearProgressIndicator(
                    progress = { value },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = PrimaryBlue,
                    trackColor = SlateBorder
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("✅ ${cat.correctAnswers} doğru", fontSize = 12.sp, color = SuccessGreen)
                    Text("❌ ${cat.questionsSolved - cat.correctAnswers} yanlış", fontSize = 12.sp, color = Color.Red.copy(alpha = 0.8f))
                    Text("📝 ${cat.questionsSolved} toplam", fontSize = 12.sp, color = SlateText)
                }
            }
        }
    }
}

// ========== HATALAR TAB ==========
@Composable
fun HatalarTab(stats: com.example.codeorbit.network.StatisticsResponse?) {
    if (stats?.mostWrongQuestions.isNullOrEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🎉", fontSize = 48.sp)
                Text("Henüz hata yok!", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Harika gidiyorsun!", fontSize = 13.sp, color = SlateText)
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "En çok yanlış yaptığın sorular",
            fontSize = 14.sp,
            color = SlateText
        )

        stats?.mostWrongQuestions?.forEach { question ->
            val iconColor = when {
                question.wrongRate >= 80 -> Color(0xFFDC2626)
                question.wrongRate >= 50 -> Color(0xFFEA580C)
                else -> PrimaryBlue
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SlateBackground)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(question.questionText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White, lineHeight = 18.sp)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(question.categoryName, fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(iconColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Hata: ${"%.0f".format(question.wrongRate)}%", fontSize = 11.sp, color = iconColor, fontWeight = FontWeight.Bold)
                    }
                    Text("${question.timesWrong}/${question.timesAnswered} kez", fontSize = 11.sp, color = SlateText)
                }
            }
        }
    }
}