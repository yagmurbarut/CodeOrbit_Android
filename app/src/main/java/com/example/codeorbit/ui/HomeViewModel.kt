package com.example.codeorbit.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val streakDays: Int = 0,
    val dailyChallengeTitle: String = "",
    val dailyChallengeXp: Int = 0,
    val recentActivities: List<QuizHistoryItem> = emptyList(),
    val errorMessage: String? = null,
    val totalQuizzes: Int = 0,
    val overallSuccessRate: Double = 0.0,
)

data class QuizHistoryItem(
    val title: String,
    val score: Int,
    val completedAt: String
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("codeorbit_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHomeData(userId: Int) {
        val effectiveUserId = if (userId == 0) RetrofitClient.userId else userId
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val stats = RetrofitClient.apiService.getUserStatistics(effectiveUserId)
                _uiState.value = _uiState.value.copy(
                    streakDays = stats.currentStreak
                )
                _uiState.value = _uiState.value.copy(
                    streakDays = stats.currentStreak,
                    totalQuizzes = stats.totalQuizzes,
                    overallSuccessRate = stats.overallSuccessRate
                )
            } catch (e: Exception) {
            }

            try {
                val challenge = RetrofitClient.apiService.getDailyChallenge(effectiveUserId)
                _uiState.value = _uiState.value.copy(
                    dailyChallengeTitle = "${challenge.categoryName} - ${challenge.difficultyLevel}",
                    dailyChallengeXp = 50
                )
            } catch (e: Exception) { }
            try {
                val history = RetrofitClient.apiService.getQuizHistory(effectiveUserId)
                val recent = history.take(2).map {
                    QuizHistoryItem(
                        title = it.categoryName,
                        score = it.score,
                        completedAt = formatDate(it.completedAt)
                    )

                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recentActivities = recent
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateStr) ?: return dateStr
            val now = java.util.Date()
            val diffMs = now.time - date.time
            val diffHours = diffMs / (1000 * 60 * 60)
            val diffDays = diffMs / (1000 * 60 * 60 * 24)
            when {
                diffHours < 1 -> "Az önce"
                diffHours < 24 -> "$diffHours saat önce"
                diffDays == 1L -> "Dün"
                diffDays < 7 -> "$diffDays gün önce"
                else -> {
                    val outputFormat = java.text.SimpleDateFormat("dd MMM", java.util.Locale("tr"))
                    outputFormat.format(date)
                }
            }
        } catch (e: Exception) {
            dateStr
        }
    }
    class HomeViewModelFactory(private val application: Application) :
        androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
    }
}