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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@Composable
fun LeaderboardScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Global", "Weekly", "Streak")

    val topThree = listOf(
        Triple("Sarah J.", "2,840 XP", 2),
        Triple("Alex Chen", "3,150 XP", 1),
        Triple("Marco R.", "2,610 XP", 3)
    )

    val otherUsers = listOf(
        Pair("Zoe Dev", "2,450 XP"),
        Pair("Liam Smith", "2,390 XP"),
        Pair("Elena K.", "2,210 XP")
    )

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
                Box(
                    modifier = Modifier.size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                // Top 3 podium
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
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
                        // 2. sıra
                        PodiumItem(
                            name = topThree[0].first,
                            xp = topThree[0].second,
                            rank = 2,
                            size = 64.dp,
                            podiumHeight = 64.dp,
                            borderColor = Color(0xFF94A3B8),
                            rankBgColor = Color(0xFF94A3B8)
                        )
                        // 1. sıra
                        PodiumItem(
                            name = topThree[1].first,
                            xp = topThree[1].second,
                            rank = 1,
                            size = 80.dp,
                            podiumHeight = 96.dp,
                            borderColor = Color(0xFFEAB308),
                            rankBgColor = Color(0xFFEAB308),
                            showCrown = true,
                            xpColor = PrimaryBlue
                        )
                        // 3. sıra
                        PodiumItem(
                            name = topThree[2].first,
                            xp = topThree[2].second,
                            rank = 3,
                            size = 64.dp,
                            podiumHeight = 48.dp,
                            borderColor = Color(0xFF92400E),
                            rankBgColor = Color(0xFF92400E)
                        )
                    }
                }

                // Diğer kullanıcılar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    otherUsers.forEachIndexed { index, (name, xp) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SlateBackground)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${index + 4}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateText,
                                modifier = Modifier.width(24.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    name.first().toString(),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(xp, fontSize = 12.sp, color = SlateText)
                            }
                            Icon(
                                if (index == 1) Icons.Filled.Remove else if (index == 2) Icons.Filled.TrendingDown else Icons.Filled.TrendingUp,
                                contentDescription = null,
                                tint = if (index == 1) SlateText else if (index == 2) Color.Red else SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Nokta nokta ayraç
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SlateBorder)
                        )
                    }

                    // Kullanıcının kendi sırası
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryBlue.copy(alpha = 0.15f))
                            .then(
                                Modifier.padding(16.dp)
                            ),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "42",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryBlue,
                            modifier = Modifier.width(24.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Y", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Sen (CoderX)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PrimaryBlue)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("PRO", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Text("845 XP · Sen #42 sıradasın", fontSize = 12.sp, color = SlateText)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+12", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
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
                    name.first().toString(),
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
        Text(name.split(" ").first(), fontSize = if (rank == 1) 14.sp else 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(xp, fontSize = 11.sp, color = xpColor)

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(if (rank == 1) 80.dp else 64.dp)
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    if (rank == 1) PrimaryBlue.copy(alpha = 0.3f)
                    else SlateBackground
                )
        )
    }
}