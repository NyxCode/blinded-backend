package com.nyxcode.blinded.backend.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Sent by a client to the server to create a new game.
 * If the operation succeeds, the server will respond with the [GameInfo] of the created game.
 * If not, an [Error] will be sent back.
 */
class CreateGame {
    companion object {
        const val NAME = "create_game"
    }

    override fun toString() = "CreateGame"
}

/**
 * Sent by a client to the server to join a game.
 * If this operation succeeds, the server will respond with the updated [GameInfo] of the game.
 * If not, an [Error] will be sent back.
 */
data class JoinGame @JsonCreator constructor(@JsonProperty("id") val id: String) {
    companion object {
        const val NAME = "join_game"
    }
}

/**
 * Sent by the server to a client to inform it that a player has joined its game.
 */
data class PlayerJoined(val gameID: String, val player: Player) {
    companion object {
        const val NAME = "player_joined"
    }
}

/**
 * Sent by a client to the server to request a bot to join a game.
 * If this operation succeeds, the server will respond with the updated [GameInfo] of the game.
 * If not, an [Error] will be sent back.
 */
data class RequestBot @JsonCreator constructor(@JsonProperty("id") val id: String) {
    companion object {
        const val NAME = "request_bot"
    }
}

/**
 * Sent by the server to a client to inform the player that his current game is completed.
 */
data class GameCompleted(val game: Game) {
    companion object {
        const val NAME = "game_completed"
    }
}

/**
 * Sent by the server to a client to inform the player that he got disqualified.
 */
data class Disqualified(val game: Game) {
    companion object {
        const val NAME = "disqualified"
    }
}

/**
 * Sent by a client to the server to do a turn.
 * If this operation succeeds, the server will respond with the updated [GameInfo] of the game.
 * If not, an [Error] will be sent back.
 */

data class DoTurn @JsonCreator constructor(@JsonProperty("player") val player: Player,
                                           @JsonProperty("gameID") val gameID: String,
                                           @JsonProperty("x") val x: Int,
                                           @JsonProperty("y") val y: Int) {
    companion object {
        const val NAME = "do_turn"
    }
}

/**
 * Sent by the server to a client to inform the player that his enemy has completed his turn.
 */
data class EnemyTurn(val x: Int, val y: Int) {
    companion object {
        const val NAME = "enemy_turn"
    }
}

/**
 * Sent by the server to a client to indicate that an error occurred.
 */
data class Error (val description: String) {
    companion object {
        const val NAME = "error"
        val GAME_NOT_FOUND = Error("We couldn't find the requested game!")
        val CANT_JOIN = Error("You can't join this game right now")
        val UNEXPECTED = Error("An unexpected error occurred")
        val NOT_YOUR_TURN = Error("It's not your turn yet!")
        val WAITING_FOR_PLAYER = Error("We are still waiting for a player to join!")
    }

    // Helps identifying an error object in non-types languages like js
    @JsonProperty("isError")
    val isError = true
}

/**
 * Sent by a client to the server to ask for statistics.
 */
class RequestStatistics {
    companion object {
        const val NAME = "request-statistics"
    }
}