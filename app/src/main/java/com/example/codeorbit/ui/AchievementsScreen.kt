package com.example.codeorbit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun AchievementsScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadBadges(effectiveUserId)
    }

    val earnedBadges = uiState.badges.filter { it.isEarned }
    val lockedBadges = uiState.badges.filter { !it.isEarned }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Achievements", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }

            // Tabs
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                listOf(
                    "Kazanılan (${earnedBadges.size})",
                    "Tümü (${uiState.badges.size})"
                ).forEachIndexed { index, tab ->
                    Box(
                        modifier = Modifier.weight(1f).clickable { selectedTab = index }.padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                tab,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == index) PrimaryBlue else SlateText
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth().height(2.dp)
                                    .background(if (selectedTab == index) PrimaryBlue else Color.Transparent)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = SlateBorder)

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Stats kartları
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${earnedBadges.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Text("KAZANILAN", fontSize = 10.sp, color = SlateText, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Column(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${lockedBadges.size}", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                            Text("KİLİTLİ", fontSize = 10.sp, color = SlateText, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }

                    if (selectedTab == 0) {
                        // Kazanılan rozetler
                        if (earnedBadges.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("🏅", fontSize = 48.sp)
                                    Text("Henüz rozet kazanılmadı", fontSize = 14.sp, color = SlateText, textAlign = TextAlign.Center)
                                    Text("Quiz çözerek rozet kazan!", fontSize = 12.sp, color = SlateText, textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Kazanılan Rozetler 🏆", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                earnedBadges.chunked(3).forEach { row ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        row.forEach { badge ->
                                            Column(
                                                modifier = Modifier.weight(1f),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(80.dp)
                                                        .clip(CircleShape)
                                                        .background(PrimaryBlue.copy(alpha = 0.3f))
                                                        .border(2.dp, PrimaryBlue.copy(alpha = 0.5f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(badge.icon ?: "🏅", fontSize = 32.sp)
                                                }
                                                Text(
                                                    badge.name,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 2
                                                )
                                                Text(
                                                    badge.description,
                                                    fontSize = 9.sp,
                                                    color = SlateText,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 2,
                                                    lineHeight = 12.sp
                                                )
                                            }
                                        }
                                        repeat(3 - row.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Tüm rozetler — kazanılan + kilitli
                        if (earnedBadges.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("✅ Kazanılanlar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                earnedBadges.forEach { badge ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(PrimaryBlue.copy(alpha = 0.08f))
                                            .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryBlue.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(badge.icon ?: "🏅", fontSize = 28.sp)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(badge.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            Text(badge.description, fontSize = 12.sp, color = SlateText, lineHeight = 16.sp)
                                        }
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(24.dp))
                                    }
                                }
                            }
                        }

                        if (lockedBadges.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("🔒 Kilitli", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                lockedBadges.forEach { badge ->
                                    val progress = if (badge.requiredCount > 0)
                                        (badge.progress.toFloat() / badge.requiredCount.toFloat()).coerceIn(0f, 1f)
                                    else 0f

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .alpha(0.7f)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.surface)
                                                    .border(2.dp, SlateBorder, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Filled.Lock, contentDescription = null, tint = SlateText, modifier = Modifier.size(24.dp))
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(badge.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Text(badge.description, fontSize = 12.sp, color = SlateText, lineHeight = 16.sp)
                                            }
                                        }
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                            color = PrimaryBlue,
                                            trackColor = SlateBorder
                                        )
                                        Text(
                                            "${badge.progress}/${badge.requiredCount} · ${"%.0f".format(progress * 100)}% tamamlandı",
                                            fontSize = 11.sp,
                                            color = PrimaryBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}