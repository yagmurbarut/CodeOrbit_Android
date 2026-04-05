package com.example.codeorbit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codeorbit.ui.AchievementsScreen
import com.example.codeorbit.ui.ActiveQuizScreen
import com.example.codeorbit.ui.FriendsScreen
import com.example.codeorbit.ui.HomeScreen
import com.example.codeorbit.ui.LeaderboardScreen
import com.example.codeorbit.ui.ProfileScreen
import com.example.codeorbit.ui.QuizResultScreen
import com.example.codeorbit.ui.QuizSetupScreen
import com.example.codeorbit.ui.StatisticsScreen
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

                NavHost(
                    navController = navController,
                    startDestination = "splash"
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
                            onNavigateToHome = {
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
                            onNavigateToHome = {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToQuiz = {
                                navController.navigate("quiz_setup")
                            },
                            onNavigateToStats = {
                                navController.navigate("statistics")
                            },
                            onNavigateToLeaderboard = {
                                navController.navigate("leaderboard")
                            },
                            onNavigateToFriends = {
                                navController.navigate("friends")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToAchievements = {
                                navController.navigate("achievements")
                            }
                        )
                    }
                    composable("quiz_setup") {
                        QuizSetupScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onStartQuiz = { _, _, _, _ ->
                                navController.navigate("active_quiz")
                            }
                        )
                    }
                    composable("active_quiz") {
                        ActiveQuizScreen(
                            onQuizFinished = { correct ->
                                navController.navigate("quiz_result/$correct/2") {
                                    popUpTo("active_quiz") { inclusive = true }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("quiz_result/{correct}/{total}") { backStackEntry ->
                        val correct = backStackEntry.arguments?.getString("correct")?.toIntOrNull() ?: 0
                        val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0
                        QuizResultScreen(
                            correctAnswers = correct,
                            totalQuestions = total,
                            onNewQuiz = {
                                navController.navigate("quiz_setup") {
                                    popUpTo("home") { inclusive = false }
                                }
                            },
                            onBackHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("statistics") {
                        StatisticsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("leaderboard") {
                        LeaderboardScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("friends") {
                        FriendsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    composable("achievements") {
                        AchievementsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}