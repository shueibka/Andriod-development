package com.example.connectfourproject.screens

import GameModel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.ServerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, viewModel: GameModel) {
    val boardGames = viewModel.boardGames.collectAsState()
    val isGameOver by viewModel.isGameOver.collectAsState()

    LaunchedEffect(true) {
        println("GameScreen.LaunchedEffect Player Ready")
        viewModel.playerReady()
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connect Four") },
                modifier = Modifier.systemBarsPadding()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display current game status
            Text(
                text = when {
                    viewModel.isDraw -> {
                        "Game is Draw!"
                    }

                    !viewModel.isWinner -> {
                        "${viewModel.currentPlayer?.name}'s Turn"
                    }

                    viewModel.isWinner -> {
                        println("this is ${viewModel.currentPlayer?.name} the winner")
                        "${viewModel.currentPlayer?.name} Won"
                    }

                    else -> {
                        "something is wrong"
                    }
                },

                style = MaterialTheme.typography.titleLarge,  //Andreas helped
                modifier = Modifier.padding(16.dp)
            )

            for (rowIndex in 0 until viewModel.rows) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (colIndex in 0 until viewModel.columns) {
                        val cellIndex = rowIndex * viewModel.columns + colIndex
                        val cellColor =
                            boardGames.value[cellIndex] // Get the cell color from ViewModel

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp)
                                .border(2.dp, Color.Black, CircleShape)
                                .background(
                                    color = cellColor, // Use the cell color from ViewModel
                                    shape = CircleShape
                                )
                                .clickable(!isGameOver) {
                                    viewModel.dropDisc(colIndex, true)
                                }
                        )
                    }
                }
            }
                Spacer(modifier = Modifier.height(16.dp))

                // Button to navigate to the "Start" screen
                Button(
                    onClick = {
                        navController.navigate("start")
                        if(!viewModel.isWinner && !viewModel.isDraw)
                            viewModel.gameFinish(GameResult.SURRENDER)
                    },
                    modifier = Modifier
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color.DarkGray),
                ) {
                    Text(
                        text = "Home",
                        fontFamily = FontFamily.Serif
                    )
                }
            }



            // Add buttons or other UI elements for game controls if necessary
        }
    }


@Composable
fun AnimatedNavigateButton(
    navController: NavController,
    onRematchClick: () -> Unit
) {
    // ... (Use the previous code for the AnimatedNavigateButton)
}
