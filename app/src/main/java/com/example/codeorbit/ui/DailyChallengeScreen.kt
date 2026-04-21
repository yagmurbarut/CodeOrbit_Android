package com.example.codeorbit.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

val categoryColors = mapOf(
    "C#" to Color(0xFF3B82F6),
    "Java" to Color(0xFFF59E0B),
    "JavaScript" to Color(0xFFF97316),
    "Python" to Color(0xFF10B981),
    "Kotlin" to Color(0xFF8B5CF6)
)

@Composable
fun DailyChallengeScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    viewModel: DailyChallengeViewModel = viewModel(
        factory = DailyChallengeViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId

    LaunchedEffect(Unit) {
        if (effectiveUserId > 0) viewModel.loadChallenge(effectiveUserId)
    }

    when (uiState.screenState) {
        ChallengeScreenState.LOADING -> {
            Box(
                modifier = Modifier.fillMaxSize().background(BackgroundDark),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                    Text("Challenge yükleniyor...", color = SlateText, fontSize = 14.sp)
                }
            }
        }
        ChallengeScreenState.INTRO -> {
            ChallengeIntroScreen(
                challenge = uiState.challenge,
                onNavigateBack = onNavigateBack,
                onSpinWheel = { viewModel.startWheelSpin() }
            )
        }
        ChallengeScreenState.WHEEL_SPINNING -> {
            WheelSpinScreen(
                categoryName = uiState.challenge?.categoryName ?: "C#",
                difficultyLevel = uiState.challenge?.difficultyLevel ?: "Easy",
                totalQuestions = uiState.challenge?.totalQuestions ?: 10,
                onStartQuiz = { viewModel.startQuiz() }
            )
        }
        ChallengeScreenState.QUIZ -> {
            ChallengeQuizScreen(
                uiState = uiState,
                effectiveUserId = effectiveUserId,
                onAnswer = { questionId, optionId -> viewModel.answerQuestion(questionId, optionId) },
                onNext = { viewModel.nextQuestion() },
                onSubmit = { viewModel.submitChallenge(effectiveUserId) }
            )
        }
        ChallengeScreenState.RESULT -> {
            ChallengeResultScreen(
                uiState = uiState,
                onNavigateBack = onNavigateBack
            )
        }
        ChallengeScreenState.ALREADY_COMPLETED -> {
            AlreadyCompletedScreen(
                challenge = uiState.challenge,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

// ========== INTRO ==========
@Composable
fun ChallengeIntroScreen(
    challenge: com.example.codeorbit.network.DailyChallengeResponse?,
    onNavigateBack: () -> Unit,
    onSpinWheel: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                Text("Daily Challenge", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("🎯", fontSize = 72.sp, textAlign = TextAlign.Center)

            Text(
                "Günlük Challenge",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                "Her gün yeni bir kategori!\nÇarkı çevir ve kategorini öğren.",
                fontSize = 15.sp,
                color = SlateText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(modifier = Modifier.weight(1f), icon = "❓", value = "${challenge?.totalQuestions ?: 10}", label = "Soru")
                InfoCard(modifier = Modifier.weight(1f), icon = "⏱️", value = "30s", label = "Her soru")
                InfoCard(modifier = Modifier.weight(1f), icon = "🏆", value = "1x", label = "Günlük")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSpinWheel,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("🎡 Çarkı Çevir!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoCard(modifier: Modifier = Modifier, icon: String, value: String, label: String) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SlateBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 24.sp)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 11.sp, color = SlateText)
    }
}

// ========== ÇARK ==========
@Composable
fun WheelSpinScreen(
    categoryName: String,
    difficultyLevel: String,
    totalQuestions: Int,
    onStartQuiz: () -> Unit
) {
    val categories = listOf("C#", "Java", "JS", "Python", "Kotlin")
    val wheelColors = listOf(
        Color(0xFF1D4ED8),
        Color(0xFF92400E),
        Color(0xFF9A3412),
        Color(0xFF065F46),
        Color(0xFF4C1D95)
    )

    val targetIndex = when (categoryName) {
        "C#" -> 0
        "Java" -> 1
        "JavaScript" -> 2
        "Python" -> 3
        "Kotlin" -> 4
        else -> 0
    }

    var isSpinning by remember { mutableStateOf(true) }
    var showResult by remember { mutableStateOf(false) }
    val rotation = remember { Animatable(0f) }

    // Gösterge üstte (açı 270) — hedef dilimin ortası targetIndex * 72 + 36
    val targetAngle = 270f - (targetIndex * 72f + 36f)
    val finalAngle = 360f * 6 + targetAngle

    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = finalAngle,
            animationSpec = tween(durationMillis = 4500, easing = FastOutSlowInEasing)
        )
        isSpinning = false
        delay(400)
        showResult = true
    }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (isSpinning) "Kategori belirleniyor..." else "Kategori belirlendi! 🎉",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Çark container
            Box(contentAlignment = Alignment.Center) {
                // Dış glow halkası
                Box(
                    modifier = Modifier
                        .size(304.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.15f))
                )

                // Çark canvas
                Canvas(
                    modifier = Modifier
                        .size(288.dp)
                        .rotate(rotation.value)
                ) {
                    val sweepAngle = 360f / 5
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f

                    // Dilimler
                    wheelColors.forEachIndexed { index, color ->
                        drawArc(
                            color = color,
                            startAngle = index * sweepAngle - 90f,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                    }

                    // Dilim çizgileri
                    wheelColors.indices.forEach { index ->
                        val angle = Math.toRadians((index * sweepAngle - 90f).toDouble())
                        drawLine(
                            color = Color(0xFF0A0F1A),
                            start = center,
                            end = Offset(
                                (center.x + radius * cos(angle)).toFloat(),
                                (center.y + radius * sin(angle)).toFloat()
                            ),
                            strokeWidth = 3f
                        )
                    }

                    // Dış halka
                    drawCircle(
                        color = Color(0xFF0A0F1A),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 8f)
                    )

                    // Merkez daire
                    drawCircle(color = Color(0xFF0A0F1A), radius = 32f, center = center)
                    drawCircle(color = Color(0xFF1E2733), radius = 24f, center = center)
                    drawCircle(color = PrimaryBlue, radius = 10f, center = center)
                }

                // Etiketler — çarkla döner
                Box(
                    modifier = Modifier
                        .size(288.dp)
                        .rotate(rotation.value)
                ) {
                    categories.forEachIndexed { index, cat ->
                        val angleDeg = index * 72f - 90f + 36f
                        val angleRad = Math.toRadians(angleDeg.toDouble())
                        val r = 90f

                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .offset(
                                    x = (144f + r * cos(angleRad) - 20f).dp,
                                    y = (144f + r * sin(angleRad) - 8f).dp
                                )
                                .width(40.dp)
                                .rotate(angleDeg + 90f)
                        )
                    }
                }
            }

            // Gösterge oku — çarkın üstünde sabit
            Box(
                modifier = Modifier
                    .offset(y = (-168).dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("▼", fontSize = 14.sp, color = Color(0xFF0A0F1A), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sonuç kartı
            if (showResult) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                categoryColors[categoryName]?.copy(alpha = 0.15f)
                                    ?: PrimaryBlue.copy(alpha = 0.15f)
                            )
                            .border(
                                2.dp,
                                categoryColors[categoryName] ?: PrimaryBlue,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎯 Bugünün Kategorisi", fontSize = 12.sp, color = SlateText, letterSpacing = 1.sp)
                            Text(
                                categoryName,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = categoryColors[categoryName] ?: PrimaryBlue
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SlateBackground)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(difficultyLevel, fontSize = 12.sp, color = SlateText, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SlateBackground)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("$totalQuestions Soru", fontSize = 12.sp, color = SlateText, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onStartQuiz,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("🚀 Challenge'ı Başlat!", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ========== QUIZ ==========
@Composable
fun ChallengeQuizScreen(
    uiState: DailyChallengeUiState,
    effectiveUserId: Int,
    onAnswer: (Int, Int) -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    val questions = uiState.challenge?.questions ?: return
    val currentQuestion = questions.getOrNull(uiState.currentQuestionIndex) ?: return
    val isLastQuestion = uiState.currentQuestionIndex == questions.size - 1
    val selectedOptionId = uiState.answers[currentQuestion.questionId]

    var timeLeft by remember(uiState.currentQuestionIndex) { mutableStateOf(30) }
    var timerRunning by remember(uiState.currentQuestionIndex) { mutableStateOf(true) }

    LaunchedEffect(uiState.currentQuestionIndex, selectedOptionId) {
        if (selectedOptionId != null) {
            timerRunning = false
            delay(1500)
            if (isLastQuestion) onSubmit() else onNext()
        }
    }

    LaunchedEffect(uiState.currentQuestionIndex) {
        timeLeft = 30
        timerRunning = true
        while (timeLeft > 0 && timerRunning) {
            delay(1000)
            timeLeft--
        }
        if (timerRunning && timeLeft == 0) {
            if (isLastQuestion) onSubmit() else onNext()
        }
    }

    val timerColor = when {
        timeLeft > 20 -> SuccessGreen
        timeLeft > 10 -> Color(0xFFFBBF24)
        else -> Color.Red
    }

    val optionLabels = listOf("A", "B", "C", "D")

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🎯 Daily Challenge", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PrimaryBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(uiState.challenge?.categoryName ?: "", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Soru ${uiState.currentQuestionIndex + 1}/${questions.size}", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Filled.Timer, contentDescription = null, tint = timerColor, modifier = Modifier.size(16.dp))
                        Text("${timeLeft}s", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = timerColor)
                    }
                }
                LinearProgressIndicator(
                    progress = { (uiState.currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = PrimaryBlue,
                    trackColor = SlateBackground
                )
                LinearProgressIndicator(
                    progress = { timeLeft / 30f },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = timerColor,
                    trackColor = SlateBackground
                )
            }

            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentQuestion.questionText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, lineHeight = 26.sp)
                Spacer(modifier = Modifier.height(8.dp))

                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionId == option.optionId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else SlateBackground)
                            .border(2.dp, if (isSelected) PrimaryBlue else SlateBorder, RoundedCornerShape(16.dp))
                            .clickable(enabled = selectedOptionId == null) {
                                onAnswer(currentQuestion.questionId, option.optionId)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue.copy(alpha = 0.3f) else Color(0xFF2A3441)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(optionLabels.getOrElse(index) { "?" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (isSelected) PrimaryBlue else SlateText)
                        }
                        Text(option.optionText, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = if (isSelected) 1f else 0.85f))
                    }
                }
            }
        }
    }
}

// ========== SONUÇ ==========
@Composable
fun ChallengeResultScreen(
    uiState: DailyChallengeUiState,
    onNavigateBack: () -> Unit
) {
    val result = uiState.result ?: return
    val successRate = result.successRate.toFloat() / 100f

    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) successRate else 0f,
        animationSpec = tween(1000),
        label = "progress"
    )

    LaunchedEffect(Unit) { animationStarted = true }

    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark)) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("🎯 Challenge Tamamlandı!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
                CircularProgressIndicator(progress = { 1f }, modifier = Modifier.size(160.dp), color = SlateBackground, strokeWidth = 10.dp, strokeCap = StrokeCap.Round)
                CircularProgressIndicator(progress = { animatedProgress }, modifier = Modifier.size(160.dp), color = PrimaryBlue, strokeWidth = 10.dp, strokeCap = StrokeCap.Round)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${"%.0f".format(result.successRate)}%", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Başarı", fontSize = 12.sp, color = SlateText)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("✅", fontSize = 24.sp)
                    Text("${result.correctAnswers}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    Text("Doğru", fontSize = 12.sp, color = SlateText)
                }
                Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("❌", fontSize = 24.sp)
                    Text("${result.totalQuestions - result.correctAnswers}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Red.copy(alpha = 0.8f))
                    Text("Yanlış", fontSize = 12.sp, color = SlateText)
                }
                Column(modifier = Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🏆", fontSize = 24.sp)
                    Text("#${result.rank}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEAB308))
                    Text("Sıra", fontSize = 12.sp, color = SlateText)
                }
            }

            if (uiState.leaderboard.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SlateBackground).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Günlük Sıralama", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    uiState.leaderboard.take(5).forEachIndexed { index, entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(when (index) { 0 -> "🥇"; 1 -> "🥈"; 2 -> "🥉"; else -> "#${entry.rank}" }, fontSize = if (index < 3) 20.sp else 14.sp, modifier = Modifier.width(32.dp))
                            Text(entry.username, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
                            Text("${entry.correctAnswers}/${entry.totalQuestions}", fontSize = 13.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Ana Sayfaya Dön", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ========== ZATEN TAMAMLANDI ==========
@Composable
fun AlreadyCompletedScreen(
    challenge: com.example.codeorbit.network.DailyChallengeResponse?,
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundDark), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("✅", fontSize = 64.sp)
            Text("Bugünkü Challenge\nTamamlandı!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
            Text("Yarın yeni bir challenge seni bekliyor!", fontSize = 14.sp, color = SlateText, textAlign = TextAlign.Center)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(PrimaryBlue.copy(alpha = 0.15f))
                    .border(2.dp, PrimaryBlue, RoundedCornerShape(16.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text("Kategori: ${challenge?.categoryName ?: ""}", fontSize = 16.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Ana Sayfaya Dön", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}