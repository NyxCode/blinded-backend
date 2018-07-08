package com.nyxcode.blinded.backend.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration
import java.time.LocalDateTime

data class Game @JsonCreator constructor(
        @JsonProperty("info") val info: GameInfo,
        @JsonProperty("data") val data: GameData) {

    infix fun isOlderThan(duration: Duration): Boolean =
            LocalDateTime.now().isAfter(info.createdAt.plus(duration))

    fun updateState() {
        val winner = data.board.winner
        if (winner != null || data.board.full) {
            info.winner = winner
            info.completed = true
        }
    }
}