package com.example.codeorbit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class StoryType {
    STREAK, DAILY_CHALLENGE, BADGE, WEEKLY_SUMMARY, FRIEND_ACTIVITY
}

data class Story(
    val id: Int,
    val type: StoryType,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val gradientColors: List<androidx.compose.ui.graphics.Color>,
    val actionRoute: String? = null,
    val isViewed: Boolean = false
)

data class StoryUiState(
    val stories: List<Story> = emptyList(),
    val isLoading: Boolean = false
)

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(StoryUiState())
    val uiState: StateFlow<StoryUiState> = _uiState

    fun loadStories(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val stories = mutableListOf<Story>()

            // 1. Streak story
            try {
                val stats = RetrofitClient.apiService.getUserStatistics(userId)
                if (stats.currentStreak > 0) {
                    stories.add(
                        Story(
                            id = 1,
                            type = StoryType.STREAK,
                            title = "${stats.currentStreak} Günlük Seri! 🔥",
                            subtitle = "Harika gidiyorsun, devam et!",
                            emoji = "🔥",
                            gradientColors = listOf(
                                androidx.compose.ui.graphics.Color(0xFFEA580C),
                                androidx.compose.ui.graphics.Color(0xFFDC2626)
                            )
                        )
                    )
                }

                // 2. Haftalık özet
                stories.add(
                    Story(
                        id = 2,
                        type = StoryType.WEEKLY_SUMMARY,
                        title = "${stats.totalQuizzes} Quiz Tamamlandı",
                        subtitle = "${"%.0f".format(stats.overallSuccessRate)}% genel başarı oranın var",
                        emoji = "📊",
                        gradientColors = listOf(
                            androidx.compose.ui.graphics.Color(0xFF4F46E5),
                            androidx.compose.ui.graphics.Color(0xFF7C3AED)
                        )
                    )
                )
            } catch (e: Exception) { }

            // 3. Daily Challenge
            try {
                val challenge = RetrofitClient.apiService.getDailyChallenge(userId)
                stories.add(
                    Story(
                        id = 3,
                        type = StoryType.DAILY_CHALLENGE,
                        title = if (challenge.hasCompleted) "Challenge Tamamlandı! ✅" else "Günlük Challenge Seni Bekliyor!",
                        subtitle = "${challenge.categoryName} · ${challenge.difficultyLevel}",
                        emoji = "🎯",
                        gradientColors = listOf(
                            androidx.compose.ui.graphics.Color(0xFF0891B2),
                            androidx.compose.ui.graphics.Color(0xFF0D9488)
                        ),
                        actionRoute = if (!challenge.hasCompleted) "daily_challenge" else null
                    )
                )
            } catch (e: Exception) { }

            // 4. Son rozet
            try {
                val badges = RetrofitClient.apiService.getUserBadges(userId)
                val earnedBadges = badges.filter { it.isEarned }
                if (earnedBadges.isNotEmpty()) {
                    val lastBadge = earnedBadges.last()
                    stories.add(
                        Story(
                            id = 4,
                            type = StoryType.BADGE,
                            title = "Rozet Kazandın! 🏆",
                            subtitle = "${lastBadge.icon ?: "🏅"} ${lastBadge.name}",
                            emoji = lastBadge.icon ?: "🏅",
                            gradientColors = listOf(
                                androidx.compose.ui.graphics.Color(0xFFD97706),
                                androidx.compose.ui.graphics.Color(0xFFB45309)
                            )
                        )
                    )
                }
            } catch (e: Exception) { }

            // 5. Arkadaş aktivitesi
            try {
                val friends = RetrofitClient.apiService.getFriends(userId)
                if (friends.isNotEmpty()) {
                    val activeFriend = friends.maxByOrNull { it.currentStreak }
                    if (activeFriend != null && activeFriend.currentStreak > 0) {
                        stories.add(
                            Story(
                                id = 5,
                                type = StoryType.FRIEND_ACTIVITY,
                                title = "${activeFriend.username} Aktif!",
                                subtitle = "${activeFriend.currentStreak} günlük serisi var 🔥",
                                emoji = "👥",
                                gradientColors = listOf(
                                    androidx.compose.ui.graphics.Color(0xFF059669),
                                    androidx.compose.ui.graphics.Color(0xFF0D9488)
                                )
                            )
                        )
                    }
                }
            } catch (e: Exception) { }

            _uiState.value = _uiState.value.copy(
                stories = stories,
                isLoading = false
            )
        }
    }

    fun markAsViewed(storyId: Int) {
        _uiState.value = _uiState.value.copy(
            stories = _uiState.value.stories.map {
                if (it.id == storyId) it.copy(isViewed = true) else it
            }
        )
    }
}

class StoryViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return StoryViewModel(application) as T
    }
}