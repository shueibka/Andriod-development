import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.ActionResult
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.ServerState
import io.garrit.android.multiplayer.SupabaseCallback
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameModel() : ViewModel(), SupabaseCallback {

    init {
        SupabaseService.callbackHandler = this
    }

    val serverState = SupabaseService.serverState
    var users: SnapshotStateList<Player> = SupabaseService.users
    var games  = SupabaseService.games
    val currentGame get() = SupabaseService.currentGame
    val user get() = SupabaseService.player

    val Me
        get() = SupabaseService.player


    val rows = 6
    val columns = 7
    private val _boardGames = MutableStateFlow(MutableList(rows * columns) { Color.White })
    val boardGames: StateFlow<List<Color>> = _boardGames


    private var currentPlayerColor = Color.Yellow


    var _isPlayerTurn by mutableStateOf(false)
    private val _isGameOver = MutableStateFlow(false)

    val isGameOver: StateFlow<Boolean> = _isGameOver
    var isWinner by mutableStateOf(false)
    var isDraw by mutableStateOf(false)

    val currentPlayer
        get() =
            if (_isPlayerTurn)
                if(Me?.id == currentGame?.player1?.id)
                    currentGame?.player1
                else
                    currentGame?.player2
            else
                if(Me?.id == currentGame?.player1?.id)
                    currentGame?.player2
                else
                    currentGame?.player1


    var _otherPlayerReadyMessage = MutableStateFlow("Waiting for the other player...")
    val otherPlayerReadyMessage: StateFlow<String> = _otherPlayerReadyMessage

    override suspend fun playerReadyHandler() {
    }

    override suspend fun actionHandler(x: Int, y: Int) {

        dropDisc(x, false)
        println("actionHandler")
    }
    override suspend fun releaseTurnHandler() {
        println("releaseTurnHandler")
            _isPlayerTurn = true

    }

    override suspend fun answerHandler(status: ActionResult) {
        println("answerHandler")

        TODO("Not yet implemented")
    }

    override suspend fun finishHandler(status: GameResult) {
        println("finishHandler")

        if(serverState.value == ServerState.GAME){
            //gameFinish(status)
        }

        when(status) {
            GameResult.DRAW -> {
                isDraw = true
            }
            GameResult.SURRENDER -> {
                _isPlayerTurn = true
                isWinner = true
            }
            GameResult.WIN -> {
                isWinner = true
            }
            GameResult.LOSE -> {
                isWinner = true
            }
        }
        _isGameOver.value = true
    }

    fun joinLobby(player: Player) {
        println("joinLobby")
        viewModelScope.launch {
            SupabaseService.joinLobby(player)
        }
    }


    fun invitePlayer(opponent: Player) {
        println("invitePlayer")

        viewModelScope.launch {
            SupabaseService.invite(opponent)
        }
    }

    fun acceptInvite(game: Game) {
        println("acceptInvite")

        viewModelScope.launch {
            SupabaseService.acceptInvite(game)
        }
    }

    fun declineInvite(game: Game) {
        println("declineInvite")

        viewModelScope.launch {
            SupabaseService.declineInvite(game)
        }
    }

    fun playerReady() {
        println("playerReady")

        viewModelScope.launch {
            SupabaseService.playerReady()
        }
    }

    fun releaseTurn() {
        println("releaseTurn")

        viewModelScope.launch {
            SupabaseService.releaseTurn()
        }
    }

    fun sendTurn(columnIndex: Int) {
        println("sendTurn")

        viewModelScope.launch {
            SupabaseService.sendTurn(columnIndex)
        }
    }


    fun gameFinish(status: GameResult) {
        println("gameFinish")
        viewModelScope.launch {
            SupabaseService.gameFinish(status)
        }
    }


    fun checkDrawCondition(): Boolean {
        println("checkDrawCondition")
        // A draw occurs when there are no empty cells (Color.White) on the board.

        return !_boardGames.value.contains(Color.White)

    }



    fun dropDisc(columnIndex: Int, isMy_Turn: Boolean) {

        // It's the player's turn, so you can make a move here
        if (isGameOver.value)
            return
        if (isMy_Turn && !_isPlayerTurn){
            return
        }

        if (checkWinCondition() || checkDrawCondition()) {
            if (checkWinCondition()) {
                handleGameWin()
            } else {
                checkDrawCondition()
            }
        } else {
            val lowestEmptyRow = findLowestEmptyRow(columnIndex)
            if (lowestEmptyRow != -1) {
                // Update board and handle game logic
                val Board = _boardGames.value.toMutableList()
                Board[lowestEmptyRow * columns + columnIndex] = currentPlayerColor
                _boardGames.value = Board

                currentPlayerColor =
                    if (currentPlayerColor == Color.Yellow) Color.Red else Color.Yellow

                if (checkWinCondition()) {
                    handleGameWin()
                    viewModelScope.launch {
                        sendTurn(columnIndex)
                        gameFinish(GameResult.WIN)
                    }
                } else if(checkDrawCondition()) {
                    viewModelScope.launch {
                        isDraw = true
                        sendTurn(columnIndex)
                        gameFinish(GameResult.DRAW)
                    }

                } else{
                    println("Player turn $_isPlayerTurn")
                    if (_isPlayerTurn) {
                        viewModelScope.launch {
                            sendTurn(columnIndex)
                            releaseTurn()
                            _isPlayerTurn = false


                        }
                    }
                }
            }
        }
    }


    suspend fun startGameAsPlayer() {
        println("startPlayer")
        _isPlayerTurn = currentGame?.player1?.id == Me?. id

        println("currentPlayerColor: ${currentPlayerColor}")
        /*if (my_Turn) {
            currentPlayer = currentGame?.player1
        }else{
            currentPlayer = currentGame?.player2
        }*/


    }


    private fun checkWinCondition(): Boolean {
        println("checkWinCondition")
        return horizontalCheck() || verticalCheck() || ascendingDiagonalCheck() || descendingDiagonalCheck()
    }


    private fun findLowestEmptyRow(columnIndex: Int): Int {
        println("findlowestEmptyRow")
        for (row in rows - 1 downTo 0) {
            if (_boardGames.value[row * columns + columnIndex] == Color.White) {
                return row
            }
        }
        return -1 // No empty row found
    }
    private fun handleGameWin() {
        println("handleGameWin")

        // Set the game result to WIN and stop further actions
        // Optionally, update a state to display the winner in the UI
        _isGameOver.value = true
        isWinner = true
        gameFinish(GameResult.LOSE)

        // You might also want to update other states to reflect the game has ended
    }
    private fun horizontalCheck(): Boolean {
        println("horizontalCheck")
        for (row in 0 until rows) {
            for (col in 0 until columns - 3) {
                val cellValue = _boardGames.value[row * columns + col]
                if (cellValue != Color.White &&
                    cellValue == _boardGames.value[row * columns + col + 1] &&
                    cellValue == _boardGames.value[row * columns + col + 2] &&
                    cellValue == _boardGames.value[row * columns + col + 3]) {
                    return true
                }
            }
        }
        return false
    }

    private fun verticalCheck(): Boolean {
        println("VerticalCheck")
        for (col in 0 until columns) {
            for (row in 0 until rows - 3) {
                val cellValue = _boardGames.value[row * columns + col]
                if (cellValue != Color.White &&
                    cellValue == _boardGames.value[(row + 1) * columns + col] &&
                    cellValue == _boardGames.value[(row + 2) * columns + col] &&
                    cellValue == _boardGames.value[(row + 3) * columns + col]) {
                    return true
                }
            }
        }
        return false
    }
    private fun ascendingDiagonalCheck(): Boolean {
        println("ascendingDiagonalCheck")
        // Loop through rows from the bottom to the third from the top
        for (row in 3 until rows) {
            // Loop through columns from left to the fourth from the right
            for (col in 0 until columns - 3) {
                val cellValue = _boardGames.value[row * columns + col]
                if (cellValue != Color.White &&
                    cellValue == _boardGames.value[(row - 1) * columns + (col + 1)] &&
                    cellValue == _boardGames.value[(row - 2) * columns + (col + 2)] &&
                    cellValue == _boardGames.value[(row - 3) * columns + (col + 3)]) {
                    return true
                }
            }
        }
        return false
    }

    private fun descendingDiagonalCheck(): Boolean {
        println("descendingDiagonalCheck")
        // Loop through rows from the top to the fourth from the bottom
        for (row in 0 until rows - 3) {
            // Loop through columns from left to the fourth from the right
            for (col in 0 until columns - 3) {
                val cellValue = _boardGames.value[row * columns + col]
                if (cellValue != Color.White &&
                    cellValue == _boardGames.value[(row + 1) * columns + (col + 1)] &&
                    cellValue == _boardGames.value[(row + 2) * columns + (col + 2)] &&
                    cellValue == _boardGames.value[(row + 3) * columns + (col + 3)]) {
                    return true
                }
            }
        }
        return false
    }
}

