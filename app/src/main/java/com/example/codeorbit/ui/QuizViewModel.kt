package com.example.codeorbit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val isLoading: Boolean = false,
    val categories: List<CategoryResponse> = emptyList(),
    val currentQuizId: Int = 0,
    val questions: List<QuestionResponse> = emptyList(),
    val categoryName: String = "",
    val errorMessage: String? = null,
    val quizStarted: Boolean = false,
    val newBadges: List<BadgeResponse> = emptyList(),
    val favoritedQuestions: Set<Int> = emptySet()
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState
    val favoritedQuestions: MutableSet<Int> = mutableSetOf()

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val cats = RetrofitClient.apiService.getAllCategories()
                _uiState.value = _uiState.value.copy(categories = cats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }
    fun toggleFavorite(userId: Int, questionId: Int) {
        viewModelScope.launch {
            try {
                if (_uiState.value.favoritedQuestions.contains(questionId)) {
                    RetrofitClient.apiService.removeFavorite(userId, questionId)
                    _uiState.value = _uiState.value.copy(
                        favoritedQuestions = _uiState.value.favoritedQuestions - questionId
                    )
                } else {
                    RetrofitClient.apiService.addFavorite(
                        com.example.codeorbit.network.AddFavoriteRequest(userId, questionId)
                    )
                    _uiState.value = _uiState.value.copy(
                        favoritedQuestions = _uiState.value.favoritedQuestions + questionId
                    )
                }
            } catch (e: Exception) { }
        }
    }
    fun startQuiz(userId: Int, categoryId: Int, difficulty: Int, questionCount: Int, favoritesOnly: Boolean, categoryName: String) {
        val effectiveUserId = RetrofitClient.getUserIdFromToken()
            .takeIf { it > 0 } ?: RetrofitClient.userId
        viewModelScope.launch {
            try {
                val favorites = RetrofitClient.apiService.getFavorites(effectiveUserId)
                val favoriteIds = favorites.map { it.questionId }.toSet()
                _uiState.value = _uiState.value.copy(favoritedQuestions = favoriteIds)
            } catch (e: Exception) { }
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.apiService.startQuiz(
                    StartQuizRequest(
                        userId = effectiveUserId,
                        categoryId = categoryId,
                        difficultyLevel = difficulty,
                        questionCount = questionCount,
                        fromFavoritesOnly = favoritesOnly
                    )
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    currentQuizId = response.quizId,
                    questions = response.questions,
                    categoryName = categoryName,
                    quizStarted = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Quiz başlatılamadı: ${e.localizedMessage}"
                )
            }
        }
    }

    fun submitAnswer(quizId: Int, quizQuestionId: Int, selectedOptionId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.submitAnswer(
                    SubmitAnswerRequest(
                        quizId = quizId,
                        quizQuestionId = quizQuestionId,
                        selectedOptionId = selectedOptionId
                    )
                )
            } catch (e: Exception) { }
        }
    }

    fun completeQuiz(quizId: Int, userId: Int, onResult: (correct: Int, total: Int) -> Unit) {
        viewModelScope.launch {
            try {
                // Önce mevcut badge'leri al
                val badgesBefore = try {
                    RetrofitClient.apiService.getUserBadges(userId)
                        .filter { it.isEarned }
                        .map { it.id }
                } catch (e: Exception) { emptyList() }

                // Quiz'i tamamla
                val result = RetrofitClient.apiService.completeQuiz(quizId)

                // Sonra yeni badge'leri al
                val badgesAfter = try {
                    RetrofitClient.apiService.getUserBadges(userId)
                        .filter { it.isEarned }
                } catch (e: Exception) { emptyList() }

                // Sadece yeni kazananları bul
                val newlyEarned = badgesAfter.filter { it.id !in badgesBefore }

                _uiState.value = _uiState.value.copy(newBadges = newlyEarned)

                onResult(result.correctAnswers, result.totalQuestions)
            } catch (e: Exception) {
                onResult(0, 0)
            }
        }
    }
    fun setAiQuestions(questions: List<QuestionResponse>) {
        _uiState.value = _uiState.value.copy(
            questions = questions,
            currentQuizId = 0,
            categoryName = "AI Quiz",
            quizStarted = false
        )
    }
    fun resetQuiz() {
        _uiState.value = QuizUiState()
    }
}

class QuizViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuizViewModel(application) as T
    }
}