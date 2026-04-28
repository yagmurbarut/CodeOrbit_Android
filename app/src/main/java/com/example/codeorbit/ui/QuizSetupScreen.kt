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
import com.example.codeorbit.ui.splash.BackgroundDark
import com.example.codeorbit.ui.splash.PrimaryBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizSetupScreen(
    userId: Int = 0,
    onNavigateBack: () -> Unit = {},
    onStartQuiz: (quizId: Int) -> Unit = {},
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(
            androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedCategoryId by remember { mutableIntStateOf(0) }
    var selectedCategoryName by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableIntStateOf(1) }
    var questionCount by remember { mutableFloatStateOf(10f) }
    var favoritesOnly by remember { mutableStateOf(false) }

    // Kategorileri yükle
    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    // Quiz başladığında navigate et
    LaunchedEffect(uiState.quizStarted) {
        if (uiState.quizStarted && uiState.currentQuizId > 0) {
            onStartQuiz(uiState.currentQuizId)
        }
    }

    // İlk kategoriyi otomatik seç
    LaunchedEffect(uiState.categories) {
        if (uiState.categories.isNotEmpty() && selectedCategoryId == 0) {
            selectedCategoryId = uiState.categories.first().categoryId
            selectedCategoryName = uiState.categories.first().name
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
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
                        .background(SlateBackground)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text(
                    text = "Quiz Setup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(40.dp))
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Kategori seçimi
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Select Category", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

                    if (uiState.categories.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
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
                                        .background(if (isSelected) PrimaryBlue else SlateBackground)
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
                                        color = if (isSelected) Color.White else SlateText
                                    )
                                }
                            }
                        }
                    }
                }

                // Zorluk seviyesi
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Difficulty Level", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SlateBackground)
                            .padding(4.dp)
                    ) {
                        listOf("Easy", "Medium", "Hard").forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selectedDifficulty == index) Color(0xFF2A3441) else Color.Transparent)
                                    .clickable { selectedDifficulty = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedDifficulty == index) PrimaryBlue else SlateText
                                )
                            }
                        }
                    }
                }

                // Soru sayısı slider
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Number of Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(questionCount.toInt().toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    Slider(
                        value = questionCount,
                        onValueChange = { questionCount = it },
                        valueRange = 5f..30f,
                        steps = 4,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = PrimaryBlue,
                            inactiveTrackColor = SlateBackground
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("5", fontSize = 12.sp, color = SlateText)
                        Text("30", fontSize = 12.sp, color = SlateText)
                    }
                }

                // Favorites toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SlateBackground)
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
                            Text("Favorites Only", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Only questions you've bookmarked", fontSize = 12.sp, color = SlateText)
                        }
                    }
                    Switch(
                        checked = favoritesOnly,
                        onCheckedChange = { favoritesOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlue)
                    )
                }

                // Hata mesajı
                uiState.errorMessage?.let {
                    Text(it, color = Color.Red.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
        }

        // Start Quiz butonu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(BackgroundDark)
                .padding(24.dp)
        ) {
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
        }
    }
}