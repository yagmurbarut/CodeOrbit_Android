package com.example.codeorbit

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codeorbit.network.RetrofitClient
import com.example.codeorbit.ui.*
import com.example.codeorbit.ui.auth.LoginScreen
import com.example.codeorbit.ui.auth.RegisterScreen
import com.example.codeorbit.ui.splash.SplashScreen
import com.example.codeorbit.ui.theme.CodeOrbitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeOrbitTheme(darkTheme = true) {
                val navController = rememberNavController()
                val context = LocalContext.current
                val prefs = context.getSharedPreferences("codeorbit_prefs", Context.MODE_PRIVATE)

                val savedToken = prefs.getString("token", "") ?: ""
                var savedUserId = prefs.getInt("userId", 0)

                if (savedUserId == 0 && savedToken.isNotEmpty()) {
                    savedUserId = try {
                        val parts = savedToken.split(".")
                        val payload = parts[1]
                        val decoded = android.util.Base64.decode(
                            payload.padEnd((payload.length + 3) / 4 * 4, '='),
                            android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
                        )
                        val json = String(decoded)
                        val key = "claims/nameidentifier\":"
                        val start = json.indexOf(key) + key.length + 1
                        val end = json.indexOf("\"", start)
                        val parsedId = json.substring(start, end).toInt()
                        prefs.edit().putInt("userId", parsedId).apply()
                        parsedId
                    } catch (e: Exception) { 0 }
                }

                android.util.Log.d("MainDebug", "savedUserId=$savedUserId")

                if (savedToken.isNotEmpty()) {
                    RetrofitClient.authToken = savedToken
                    RetrofitClient.userId = savedUserId
                }

                var userId by remember { mutableStateOf(savedUserId) }
                var username by remember { mutableStateOf(prefs.getString("username", "") ?: "") }

                val startDestination = if (savedToken.isEmpty()) "splash" else "home"

                val quizViewModel: QuizViewModel = viewModel(
                    factory = QuizViewModelFactory(
                        context.applicationContext as android.app.Application
                    )
                )

                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(
                        context.applicationContext as android.app.Application
                    )
                )

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("splash") {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate("register")
                            },
                            onNavigateToHome = { newUserId, newUsername ->
                                userId = newUserId
                                username = newUsername
                                RetrofitClient.userId = newUserId
                                userViewModel.clearData()
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            onNavigateToHome = { newUserId, newUsername ->
                                userId = newUserId
                                username = newUsername
                                RetrofitClient.userId = newUserId
                                userViewModel.clearData()
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            userId = userId,
                            username = username,
                            onNavigateToQuiz = { navController.navigate("quiz_setup") },
                            onNavigateToStats = { navController.navigate("statistics") },
                            onNavigateToLeaderboard = { navController.navigate("leaderboard") },
                            onNavigateToFriends = { navController.navigate("friends") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToAchievements = { navController.navigate("achievements") },
                            onNavigateToChallenge = { navController.navigate("daily_challenge") },
                            onNavigateToNotifications = { navController.navigate("notifications") }
                        )
                    }
                    composable("quiz_setup") {
                        QuizSetupScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            onStartQuiz = { quizId ->
                                navController.navigate("active_quiz/$quizId")
                            },
                            viewModel = quizViewModel
                        )
                    }
                    composable("active_quiz/{quizId}") { backStackEntry ->
                        val quizId = backStackEntry.arguments?.getString("quizId")?.toIntOrNull() ?: 0
                        ActiveQuizScreen(
                            quizId = quizId,
                            userId = userId,
                            onQuizFinished = { correct, total ->
                                val categoryName = quizViewModel.uiState.value.categoryName
                                navController.navigate("quiz_result/$correct/$total/$categoryName") {
                                    popUpTo("active_quiz/$quizId") { inclusive = true }
                                }
                            },
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = quizViewModel
                        )
                    }
                    composable("quiz_result/{correct}/{total}/{categoryName}") { backStackEntry ->
                        val correct = backStackEntry.arguments?.getString("correct")?.toIntOrNull() ?: 0
                        val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
                        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
                        val quizUiState by quizViewModel.uiState.collectAsState()
                        QuizResultScreen(
                            correctAnswers = correct,
                            totalQuestions = total,
                            categoryName = categoryName,
                            newBadges = quizUiState.newBadges,
                            onNewQuiz = {
                                quizViewModel.resetQuiz()
                                navController.navigate("quiz_setup") {
                                    popUpTo("home") { inclusive = false }
                                }
                            },
                            onBackHome = {
                                quizViewModel.resetQuiz()
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("statistics") {
                        StatisticsScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                    composable("leaderboard") {
                        LeaderboardScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            userId = userId,
                            username = username,
                            onNavigateToAccountSettings = { navController.navigate("account_settings") },
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToFavorites = { navController.navigate("favorites") },
                            onLogout = {
                                prefs.edit().clear().apply()
                                RetrofitClient.authToken = ""
                                RetrofitClient.userId = 0
                                userViewModel.clearData()
                                val intent = android.content.Intent(context, MainActivity::class.java).apply {
                                    flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                                            android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                            },
                            viewModel = userViewModel
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                    composable("friends") {
                        FriendsScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                    composable("achievements") {
                        AchievementsScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                    composable("daily_challenge") {
                        DailyChallengeScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("favorites") {
                        FavoritesScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                    composable("account_settings") {
                        AccountSettingsScreen(
                            userId = userId,
                            currentUsername = username,
                            onNavigateBack = { navController.popBackStack() },
                            onUsernameUpdated = { newName ->
                                username = newName
                                // prefs'e de kaydet
                                prefs.edit().putString("username", newName).apply()
                            },
                            viewModel = userViewModel
                        )
                    }
                    composable("notifications") {
                        NotificationScreen(
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = userViewModel
                        )
                    }
                }
            }
        }
    }
}