package com.example.codeorbit.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val PrimaryBlue = Color(0xFF4B8FE2)
val BackgroundDark = Color(0xFF121820)
val BackgroundLight = Color(0xFFF6F7F8)

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {

    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        progress = 1f
        delay(2500)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Arka plan glow efektleri
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-80).dp, y = (-120).dp)
                .background(PrimaryBlue.copy(alpha = 0.08f), CircleShape)
                .blur(60.dp)
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = 100.dp, y = 150.dp)
                .background(PrimaryBlue.copy(alpha = 0.05f), CircleShape)
                .blur(80.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Logo alanı
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Dış halka
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.Transparent)
                        .clip(CircleShape)
                        .then(
                            Modifier.background(
                                PrimaryBlue.copy(alpha = 0.1f),
                                CircleShape
                            )
                        )
                )
                // Orta halka
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape)
                )
                // İç glow
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(PrimaryBlue.copy(alpha = 0.2f), CircleShape)
                        .blur(20.dp)
                )
                // Merkez ikon kutusu
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            BackgroundDark,
                            RoundedCornerShape(16.dp)
                        )
                        .then(
                            Modifier.background(
                                PrimaryBlue.copy(alpha = 0.0f),
                                RoundedCornerShape(16.dp)
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Code,
                        contentDescription = "CodeOrbit",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Başlık
            Text(
                text = buildString {
                    append("Code")
                    append("Orbit")
                },
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Slogan
            Text(
                text = "CODE, LEARN, COMPETE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Alt loading bölümü
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
                    .padding(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Initializing orbit...",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PrimaryBlue,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "v1.0.0 • System Ready",
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}