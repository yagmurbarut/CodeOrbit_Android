package com.example.codeorbit.ui.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeorbit.network.LoginRequest
import com.example.codeorbit.network.RegisterRequest
import com.example.codeorbit.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val userId: Int = 0,
    val username: String = "",
    val token: String = ""
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("codeorbit_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        val savedToken = prefs.getString("token", "") ?: ""
        if (savedToken.isNotEmpty()) {
            RetrofitClient.authToken = savedToken
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.apiService.login(
                    LoginRequest(email = email, password = password)
                )
                RetrofitClient.authToken = response.token
                RetrofitClient.userId = response.userId
                prefs.edit()
                    .putString("token", response.token)
                    .putInt("userId", response.userId)
                    .putString("username", response.username)
                    .apply()
                android.util.Log.d("AuthDebug", "Login userId=${response.userId} username=${response.username}")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    userId = response.userId,
                    username = response.username,
                    token = response.token
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Giriş başarısız: ${e.localizedMessage}"
                )
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = RetrofitClient.apiService.register(
                    RegisterRequest(username = username, email = email, password = password)
                )
                RetrofitClient.authToken = response.token
                RetrofitClient.userId = response.userId
                prefs.edit()
                    .putString("token", response.token)
                    .putInt("userId", response.userId)
                    .putString("username", response.username)
                    .apply()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    userId = response.userId,
                    username = response.username,
                    token = response.token
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Kayıt başarısız: ${e.localizedMessage}"
                )
            }
        }
    }

    fun logout() {
        RetrofitClient.authToken = ""
        RetrofitClient.userId = 0
        prefs.edit().clear().apply()
        _uiState.value = AuthUiState()
    }

    fun resetState() {
        _uiState.value = AuthUiState()
    }
}
class AuthViewModelFactory(private val application: Application) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(application) as T
    }
}