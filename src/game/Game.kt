package com.nyxcode.blinded.backend.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

data class Game @JsonCreator constructor(
        @JsonProperty("info") val info: GameInfo,
        @JsonProperty("board") val board: GameBoard = newBoard()) {

    infix fun isOlderThan(duration: Duration): Boolean =
            LocalDateTime.now().isAfter(info.createdAt.plus(duration))

    fun updateState() {
        val winner = board.winner
        if (winner != null || board.full) {
            info.winner = winner
            info.completed = true
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (info != other.info) return false
        if (!Arrays.equals(board, other.board)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = info.hashCode()
        result = 31 * result + Arrays.hashCode(board)
        return result
    }
}

val Game.id get() = info.id