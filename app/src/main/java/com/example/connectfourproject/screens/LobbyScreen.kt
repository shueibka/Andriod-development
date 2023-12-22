package com.example.connectfourproject.screens

import GameModel
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.ServerState
import io.garrit.android.multiplayer.SupabaseService

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LobbyScreen(navController: NavController, viewModel: GameModel) {
    val players = viewModel.users
    val gameChallenges = viewModel.games
    val serverState = viewModel.serverState.collectAsState()

    LaunchedEffect(serverState.value) {
        when (serverState.value) {
            ServerState.LOADING_GAME, ServerState.GAME -> {
                navController.navigate("game")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose a player to challenge") },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(100.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(30.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(players) { player ->

                    if (player != viewModel.user)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = player.name)
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                viewModel.invitePlayer(player)
                            },
                        ) {
                            Text(text = "challenge",)
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                items(gameChallenges) { challenger ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = challenger.player1.name.trim() + " has challenged you!")
                    }

                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.acceptInvite(challenger)
                                navController.navigate("game")
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp, vertical = 5.dp)
                        ) {
                            Text(text = "Accept",)
                        }
                        Button(
                            onClick = {
                                viewModel.declineInvite(challenger)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 5.dp, vertical = 5.dp)
                        ) {
                            Text(text = "decline")
                        }
                    }
                }
            }
        }
    }
}


