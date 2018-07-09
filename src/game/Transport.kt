package com.nyxcode.blinded.backend.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Client => Server
 *
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
 * Client => Server
 *
 * Sent by a client to the server to join a game.
 * If this operation succeeds, the server will respond with the updated [GameInfo] of the game.
 * If not, an [Error] will be sent back.
 */
data class JoinGame @JsonCreator constructor(@JsonProperty("id") val id: String) {
    companion object {
        const val NAME = "join_game"
    }
}

data class PlayerJoined @JsonCreator constructor(@JsonProperty("player") val player: Player) {
    companion object {
        const val NAME = "player_joined"
    }
}

/**
 * Client => Server
 *
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
 * Server => Client
 *
 * Sent by the server to a client to inform the player that his current game is completed.
 */
data class GameCompleted @JsonCreator constructor(@JsonProperty("game") val game: Game) {
    companion object {
        const val NAME = "game_completed"
    }
}

/**
 * Server => Client
 *
 * Sent by the server to a client to inform the player that he got disqualified.
 */
data class Disqualified @JsonCreator constructor(@JsonProperty("reason") val reason: String,
                                                 @JsonProperty("game") val game: Game) {
    companion object {
        const val NAME = "disqualified"
    }
}

/**
 * Client => Server
 *
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
 * Server => Client
 *
 * Sent by the server to a client to inform the player that his enemy has completed his turn.
 */
data class EnemyTurn @JsonCreator constructor(@JsonProperty("x") val x: Int,
                                              @JsonProperty("y") val y: Int) {
    companion object {
        const val NAME = "enemy_turn"
    }
}

/**
 * Server => Client
 *
 * Sent by the server to a client to indicate that an error occurred.
 */
data class Error @JsonCreator constructor(@JsonProperty("description") val description: String) {
    companion object {
        const val NAME = "error"
        val GAME_NOT_FOUND = Error("We couldn't find the requested game!")
    }

    // Helps identifying an error object in non-types languages like js
    val isError = true
}

/**
 * Client => Server
 *
 * Sent by a client to the server to ask for statistics.
 */
class RequestStatistics {
    companion object {
        const val NAME = "request-statistics"
    }
}