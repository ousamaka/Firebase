package com.example.firebase

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.firebase.presentation.chat.ChatScreen
import com.example.firebase.presentation.homescreen.HomeScreen
import com.example.firebase.presentation.homescreen.HomeViewmodel
import com.example.firebase.presentation.initial.InitialScreen
import com.example.firebase.presentation.login.LoginScreen
import com.example.firebase.presentation.map.MapScreen
import com.example.firebase.presentation.profile.ProfileScreen
import com.example.firebase.presentation.signup.SignupScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth, homeViewModel: HomeViewmodel) {
    NavHost(navHostController, startDestination = "Initial") {
        composable("Initial") {
            InitialScreen(
                navigateToLogin = { navHostController.navigate("Login") },
                navigateToSignUp = { navHostController.navigate("Signup") }
            )
        }
        composable("Login") {
            LoginScreen(auth = auth, navigateToHome = { navHostController.navigate("home") })
        }
        composable("Signup") {
            SignupScreen(auth = auth)
        }
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                navigateToProfile = { navHostController.navigate("profile") },
                navigateToChat = { navHostController.navigate("chat") },
                navigateToMap = { navHostController.navigate("map") }
            )
        }
        composable("profile") { ProfileScreen(onBack = { navHostController.popBackStack() }) }
        composable("chat") { ChatScreen(onBack = { navHostController.popBackStack() }) }
        composable("map") { MapScreen(onBack = { navHostController.popBackStack() }, homeViewModel = homeViewModel) }
    }
}