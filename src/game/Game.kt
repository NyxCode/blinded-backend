package com.nyxcode.blinded.backend.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.nyxcode.blinded.backend.LOG
import com.nyxcode.blinded.backend.now
import java.time.Duration

data class Game @JsonCreator constructor(@JsonProperty("info") val info: GameInfo,
                                         @JsonProperty("data") val data: GameData) {
    infix fun isOlderThan(duration: Duration): Boolean =
            now().isAfter(info.createdAt.plus(duration))

    fun updateState() {
        if(info.completed) return
        val (isCompleted, winner) = getWinner()
        if(isCompleted) {
            LOG.info("Game ${info.id} was won by ${winner ?: "no-one (ended in a draw)"}")
            info.winner = winner
            info.completed = true
        }
    }

    private fun getWinner(): Pair<Boolean, Player?> {
        val board = data.board
        // check rows and columns
        for (i in 0..2) {
            when {
                eq(board[0][i], board[1][i], board[2][i]) -> return Pair(true, board[0][i])
                eq(board[i][0], board[i][1], board[i][2]) -> return Pair(true, board[i][0])
            }
        }
        // check diagonal or if the board is full
        return when {
            eq(board[0][0], board[1][1], board[2][2]) -> Pair(true, board[0][0])
            eq(board[2][0], board[1][1], board[0][2]) -> Pair(true, board[2][0])
            board.full -> Pair(true, null)
            else -> Pair(false, null)
        }
    }

    private val Array<Array<Player?>>.full
        get() = this[0][0] != null && this[1][0] != null && this[2][0] != null
                && this[0][1] != null && this[1][1] != null && this[2][1] != null
                && this[0][2] != null && this[1][2] != null && this[2][2] != null

    private fun eq(str1: String?, str2: String?, str3: String?) = str1 != null && str1 == str2 && str2 == str3
}