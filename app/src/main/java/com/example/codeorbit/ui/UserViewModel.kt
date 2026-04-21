package com.example.codeorbit.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val statistics: StatisticsResponse? = null,
    val badges: List<BadgeResponse> = emptyList(),
    val globalLeaderboard: List<LeaderboardResponse> = emptyList(),
    val weeklyLeaderboard: List<LeaderboardResponse> = emptyList(),
    val friends: List<FriendResponse> = emptyList(),
    val notifications: List<NotificationResponse> = emptyList(),
    val searchResults: List<FriendSearchResponse> = emptyList(),
    val friendRequests: List<FriendRequestResponse> = emptyList(),
    val friendRequestSent: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<FavoriteQuestionResponse> = emptyList()
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState
    val favorites: List<FavoriteQuestionResponse> = emptyList()

    fun loadStatistics(userId: Int) {
        android.util.Log.d("StatsDebug", "loadStatistics called with userId=$userId")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val stats = RetrofitClient.apiService.getUserStatistics(userId)
                _uiState.value = _uiState.value.copy(isLoading = false, statistics = stats)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }
    fun loadFavorites(userId: Int) {
        viewModelScope.launch {
            try {
                val favorites = RetrofitClient.apiService.getFavorites(userId)
                _uiState.value = _uiState.value.copy(favorites = favorites)
            } catch (e: Exception) { }
        }
    }

    fun removeFavorite(userId: Int, questionId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.removeFavorite(userId, questionId)
                _uiState.value = _uiState.value.copy(
                    favorites = _uiState.value.favorites.filter { it.questionId != questionId }
                )
            } catch (e: Exception) { }
        }
    }
    fun clearData() {
        _uiState.value = UserUiState()
    }

    fun loadBadges(userId: Int) {
        viewModelScope.launch {
            try {
                val badges = RetrofitClient.apiService.getUserBadges(userId)
                _uiState.value = _uiState.value.copy(badges = badges)
            } catch (e: Exception) { }
        }
    }

    fun loadLeaderboard(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val global = RetrofitClient.apiService.getGlobalLeaderboard(userId)
                val weekly = RetrofitClient.apiService.getWeeklyLeaderboard(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    globalLeaderboard = global,
                    weeklyLeaderboard = weekly
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun loadFriends(userId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val friends = RetrofitClient.apiService.getFriends(userId)
                _uiState.value = _uiState.value.copy(isLoading = false, friends = friends)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun loadFriendRequests(userId: Int) {
        viewModelScope.launch {
            try {
                val requests = RetrofitClient.apiService.getFriendRequests(userId)
                _uiState.value = _uiState.value.copy(friendRequests = requests)
            } catch (e: Exception) { }
        }
    }

    fun respondFriendRequest(userId: Int, requestId: Int, accept: Boolean) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.respondFriendRequest(
                    userId = userId,
                    request = RespondFriendRequestRequest(
                        requestId = requestId,
                        accept = accept
                    )
                )
                loadFriendRequests(userId)
                if (accept) loadFriends(userId)
            } catch (e: Exception) { }
        }
    }

    fun searchUsers(searchTerm: String, currentUserId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = RetrofitClient.apiService.searchUsers(searchTerm, currentUserId)
                _uiState.value = _uiState.value.copy(
                    isSearching = false,
                    searchResults = results
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSearching = false)
            }
        }
    }

    fun sendFriendRequest(senderId: Int, receiverId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.sendFriendRequest(
                    SendFriendRequestRequest(
                        senderId = senderId,
                        receiverId = receiverId
                    )
                )
                _uiState.value = _uiState.value.copy(friendRequestSent = true)
            } catch (e: Exception) { }
        }
    }
}

class UserViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return UserViewModel(application) as T
    }
}