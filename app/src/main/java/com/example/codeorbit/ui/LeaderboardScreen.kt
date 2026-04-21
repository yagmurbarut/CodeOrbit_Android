package com.example.codeorbit.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun LeaderboardScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Global", "Weekly")
    val uiState by viewModel.uiState.collectAsState()

    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
    LaunchedEffect(effectiveUserId) {
        if (effectiveUserId > 0) viewModel.loadLeaderboard(effectiveUserId)
    }

    val activeList = if (selectedTab == 0) uiState.globalLeaderboard else uiState.weeklyLeaderboard

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        .clip(CircleShape)
                        .background(SlateBackground)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Leaderboard", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            } else if (activeList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz sıralama yok", fontSize = 14.sp, color = SlateText)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Podium — sadece 3+ kullanıcı varsa göster
                    if (activeList.size >= 3) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(PrimaryBlue.copy(alpha = 0.1f), Color.Transparent)
                                    )
                                )
                                .padding(horizontal = 16.dp, vertical = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                PodiumItem(
                                    name = activeList[1].username,
                                    xp = "${activeList[1].score} Quiz",
                                    rank = 2,
                                    size = 64.dp,
                                    podiumHeight = 64.dp,
                                    borderColor = Color(0xFF94A3B8),
                                    rankBgColor = Color(0xFF94A3B8)
                                )
                                PodiumItem(
                                    name = activeList[0].username,
                                    xp = "${activeList[0].score} Quiz",
                                    rank = 1,
                                    size = 80.dp,
                                    podiumHeight = 96.dp,
                                    borderColor = Color(0xFFEAB308),
                                    rankBgColor = Color(0xFFEAB308),
                                    showCrown = true,
                                    xpColor = PrimaryBlue
                                )
                                PodiumItem(
                                    name = activeList[2].username,
                                    xp = "${activeList[2].score} Quiz",
                                    rank = 3,
                                    size = 64.dp,
                                    podiumHeight = 48.dp,
                                    borderColor = Color(0xFF92400E),
                                    rankBgColor = Color(0xFF92400E)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Tüm liste
                    activeList.forEach { entry ->
                        val isMe = entry.userId == effectiveUserId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isMe) PrimaryBlue.copy(alpha = 0.15f)
                                    else SlateBackground
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Sıra numarası
                            Text(
                                "${entry.rank}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (entry.rank) {
                                    1 -> Color(0xFFEAB308)
                                    2 -> Color(0xFF94A3B8)
                                    3 -> Color(0xFF92400E)
                                    else -> if (isMe) PrimaryBlue else SlateText
                                },
                                modifier = Modifier.width(28.dp)
                            )
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isMe) PrimaryBlue.copy(alpha = 0.4f)
                                        else PrimaryBlue.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    entry.username.firstOrNull()?.toString()?.uppercase() ?: "?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            // İsim ve skor
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (isMe) "${entry.username} (Sen)" else entry.username,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        "${entry.score} Quiz",
                                        fontSize = 12.sp,
                                        color = SlateText
                                    )
                                    Text(
                                        "${"%.0f".format(entry.successRate)}% başarı",
                                        fontSize = 12.sp,
                                        color = SuccessGreen
                                    )
                                }
                            }
                            // Streak
                            if (entry.currentStreak > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text("🔥", fontSize = 14.sp)
                                    Text(
                                        "${entry.currentStreak}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF97316)
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

@Composable
fun PodiumItem(
    name: String,
    xp: String,
    rank: Int,
    size: androidx.compose.ui.unit.Dp,
    podiumHeight: androidx.compose.ui.unit.Dp,
    borderColor: Color,
    rankBgColor: Color,
    showCrown: Boolean = false,
    xpColor: Color = SlateText
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (showCrown) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color(0xFFEAB308), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
        } else {
            Spacer(modifier = Modifier.height(28.dp))
        }

        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(borderColor.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    name.firstOrNull()?.toString() ?: "?",
                    fontSize = if (rank == 1) 28.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .size(if (rank == 1) 28.dp else 24.dp)
                    .clip(CircleShape)
                    .background(rankBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$rank",
                    fontSize = if (rank == 1) 13.sp else 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            name.split(" ").first(),
            fontSize = if (rank == 1) 14.sp else 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(xp, fontSize = 11.sp, color = xpColor)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(if (rank == 1) 80.dp else 64.dp)
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    if (rank == 1) PrimaryBlue.copy(alpha = 0.3f) else SlateBackground
                )
        )
    }
}