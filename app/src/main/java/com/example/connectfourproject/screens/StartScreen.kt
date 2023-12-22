package com.example.connectfourproject.screens

import GameModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.garrit.android.multiplayer.Player

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(navController: NavController, viewModel: GameModel) {
    var name by remember { mutableStateOf("") }
    Scaffold (
        topBar = {
            TopAppBar( // Already new from the noteApp
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 100.dp),
                title = { Text("Connect four")
                }
            )
        },
    ) { paddingValues ->
        Row (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.LightGray)
        ) {
            Column (  // Already new from the noteApp
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 30.dp)
                )  {
                    TextField( // Already new from the noteApp
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(33.dp))
                            .padding(6.dp),
                        value = name,
                        onValueChange = { newName ->
                            name = newName
                        },
                        label = { Text("Write your name") }
                    )
                    Button( // Already new from the noteApp
                        onClick = {
                            viewModel.joinLobby(Player(name = name))
                            navController.navigate("lobby")
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp)
                    ) {
                        Text("Play")
                    }
                }
            }
        }
    }
