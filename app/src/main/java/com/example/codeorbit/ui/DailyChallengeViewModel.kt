package com.example.codeorbit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class ChallengeScreenState {
    LOADING, INTRO, WHEEL_SPINNING, QUIZ, RESULT, ALREADY_COMPLETED
}

data class DailyChallengeUiState(
    val screenState: ChallengeScreenState = ChallengeScreenState.LOADING,
    val challenge: DailyChallengeResponse? = null,
    val currentQuestionIndex: Int = 0,
    val answers: MutableMap<Int, Int> = mutableMapOf(),
    val result: DailyChallengeResultResponse? = null,
    val leaderboard: List<ChallengeLeaderboardResponse> = emptyList(),
    val errorMessage: String? = null
)

class DailyChallengeViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DailyChallengeUiState())
    val uiState: StateFlow<DailyChallengeUiState> = _uiState

    fun loadChallenge(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(screenState = ChallengeScreenState.LOADING)
            try {
                val challenge = RetrofitClient.apiService.getDailyChallenge(userId)
                if (challenge.hasCompleted) {
                    _uiState.value = _uiState.value.copy(
                        screenState = ChallengeScreenState.ALREADY_COMPLETED,
                        challenge = challenge
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        screenState = ChallengeScreenState.INTRO,
                        challenge = challenge
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.localizedMessage,
                    screenState = ChallengeScreenState.INTRO
                )
            }
        }
    }

    fun startWheelSpin() {
        _uiState.value = _uiState.value.copy(screenState = ChallengeScreenState.WHEEL_SPINNING)
    }

    fun startQuiz() {
        _uiState.value = _uiState.value.copy(screenState = ChallengeScreenState.QUIZ)
    }

    fun answerQuestion(questionId: Int, optionId: Int) {
        val newAnswers = _uiState.value.answers.toMutableMap()
        newAnswers[questionId] = optionId
        _uiState.value = _uiState.value.copy(answers = newAnswers)
    }

    fun nextQuestion() {
        val current = _uiState.value.currentQuestionIndex
        val total = _uiState.value.challenge?.questions?.size ?: 0
        if (current < total - 1) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = current + 1)
        }
    }

    fun submitChallenge(userId: Int) {
        val challenge = _uiState.value.challenge ?: return
        val answers = _uiState.value.answers

        viewModelScope.launch {
            try {
                val request = SubmitChallengeRequest(
                    userId = userId,
                    dailyChallengeId = challenge.challengeId,
                    answers = answers.map { (questionId, optionId) ->
                        ChallengeAnswerRequest(
                            questionId = questionId,
                            selectedOptionId = optionId
                        )
                    }
                )
                val result = RetrofitClient.apiService.submitChallenge(request)

                // Leaderboard'u da çek
                val leaderboard = try {
                    RetrofitClient.apiService.getChallengeLeaderboard()
                } catch (e: Exception) { emptyList() }

                _uiState.value = _uiState.value.copy(
                    screenState = ChallengeScreenState.RESULT,
                    result = result,
                    leaderboard = leaderboard
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.localizedMessage)
            }
        }
    }
}

class DailyChallengeViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DailyChallengeViewModel(application) as T
    }
}