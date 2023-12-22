package com.example.connectfourproject

import GameModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.connectfourproject.screens.GameScreen
import com.example.connectfourproject.screens.LobbyScreen
import com.example.connectfourproject.screens.StartScreen
import com.example.connectfourproject.ui.theme.ConnectFourProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectFourProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    myApp()
                }
            }
        }
    }
}

@Composable
fun  myApp() {
    val navController = rememberNavController()
    val viewModel = GameModel()
    NavHost(
        navController = navController,
        startDestination = "start"
    ) {
        composable("start") {
            StartScreen(navController,viewModel )
        }
        composable("lobby") {
            LobbyScreen(navController, viewModel )
        }
        composable("game") {
            LaunchedEffect(true ){// Andreas helped
                viewModel.startGameAsPlayer()
            }
            GameScreen(navController, viewModel)
        }

    }
}