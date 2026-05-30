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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.codeorbit.network.GenerateQuestionRequest
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.splash.PrimaryBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizSetupScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    onStartQuiz: (quizId: Int) -> Unit = {},
    onStartAiQuiz: (questions: List<com.example.codeorbit.network.QuestionResponse>) -> Unit = {},
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategoryId by remember { mutableIntStateOf(0) }
    var selectedCategoryName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableIntStateOf(1) }
    var questionCount by remember { mutableFloatStateOf(10f) }
    var favoritesOnly by remember { mutableStateOf(false) }
    var aiIsLoading by remember { mutableStateOf(false) }
    var aiError by remember { mutableStateOf<String?>(null) }

    val difficultyLabels = listOf("Easy", "Medium", "Hard")

    LaunchedEffect(Unit) { viewModel.loadCategories() }

    LaunchedEffect(uiState.quizStarted) {
        if (uiState.quizStarted && uiState.currentQuizId > 0) {
            onStartQuiz(uiState.currentQuizId)
        }
    }

    LaunchedEffect(uiState.categories) {
        if (uiState.categories.isNotEmpty() && selectedCategoryId == 0) {
            selectedCategoryId = uiState.categories.first().categoryId
            selectedCategoryName = uiState.categories.first().name
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "Quiz Setup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            // Sekmeler
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(4.dp)
            ) {
                listOf("Normal Quiz", "🤖 AI Quiz").forEachIndexed { index, label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedTab == index) PrimaryBlue else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Kategori seçimi — her iki sekmede de ortak
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Select Category", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    if (uiState.categories.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            uiState.categories.forEach { category ->
                                val isSelected = selectedCategoryId == category.categoryId
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surface)
                                        .clickable {
                                            selectedCategoryId = category.categoryId
                                            selectedCategoryName = category.name
                                        }
                                        .padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = category.name,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Zorluk seviyesi — her iki sekmede ortak
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Difficulty Level", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(4.dp)
                    ) {
                        difficultyLabels.forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedDifficulty == index) MaterialTheme.colorScheme.background else Color.Transparent)
                                    .clickable { selectedDifficulty = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedDifficulty == index) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (selectedTab == 0) {
                    // Normal Quiz — soru sayısı ve favoriler
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("Number of Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text(questionCount.toInt().toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Slider(
                            value = questionCount,
                            onValueChange = { questionCount = it },
                            valueRange = 5f..30f,
                            steps = 4,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryBlue,
                                activeTrackColor = PrimaryBlue,
                                inactiveTrackColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("5", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("30", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Favorites Only", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                                Text("Only questions you've bookmarked", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = favoritesOnly,
                            onCheckedChange = { favoritesOnly = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                        )
                    }
                } else {
                    // AI Quiz açıklaması
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PrimaryBlue.copy(alpha = 0.1f))
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🤖", fontSize = 32.sp)
                        Text(
                            "AI ile Quiz",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            "Claude AI seçtiğin kategori ve zorluğa göre özel sorular üretir. Her seferinde farklı ve yeni sorularla karşılaşırsın!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    }

                    // Soru sayısı AI için
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("Number of Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text(questionCount.toInt().toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Slider(
                            value = questionCount,
                            onValueChange = { questionCount = it },
                            valueRange = 3f..10f,
                            steps = 2,
                            modifier = Modifier.fillMaxWidth(),
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryBlue,
                                activeTrackColor = PrimaryBlue,
                                inactiveTrackColor = MaterialTheme.colorScheme.surface
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("3", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("10", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    aiError?.let {
                        Text(it, color = Color.Red.copy(alpha = 0.8f), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }

                uiState.errorMessage?.let {
                    Text(it, color = Color.Red.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
        }

        // Alt buton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
        ) {
            if (selectedTab == 0) {
                Button(
                    onClick = {
                        if (selectedCategoryId > 0) {
                            viewModel.startQuiz(
                                userId = userId,
                                categoryId = selectedCategoryId,
                                difficulty = selectedDifficulty,
                                questionCount = questionCount.toInt(),
                                favoritesOnly = favoritesOnly,
                                categoryName = selectedCategoryName
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = !uiState.isLoading && selectedCategoryId > 0
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Start Quiz", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            } else {
                Button(
                    onClick = {
                        if (selectedCategoryId > 0 && !aiIsLoading) {
                            aiError = null
                            aiIsLoading = true
                            scope.launch {
                                try {
                                    val request = GenerateQuestionRequest(
                                        categoryName = selectedCategoryName,
                                        difficultyLevel = difficultyLabels[selectedDifficulty],
                                        questionType = "MultipleChoice",
                                        count = questionCount.toInt(),
                                        categoryId = selectedCategoryId
                                    )
                                    val response = RetrofitClient.apiService.generateQuestions(request)
                                    val questions = response.questions.map { q ->
                                        com.example.codeorbit.network.QuestionResponse(
                                            quizQuestionId = q.correctOptionIndex,
                                            questionId = q.id,
                                            questionText = q.questionText,
                                            questionType = q.questionType,
                                            options = q.options
                                        )
                                    }
                                    onStartAiQuiz(questions)
                                } catch (e: Exception) {
                                    aiError = "Sorular üretilemedi: ${e.localizedMessage}"
                                } finally {
                                    aiIsLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    enabled = !aiIsLoading && selectedCategoryId > 0
                ) {
                    if (aiIsLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Sorular Üretiyor...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("🤖 AI Quiz Başlat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}