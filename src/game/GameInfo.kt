package com.nyxcode.blinded.backend.game

import com.corundumstudio.socketio.BroadcastOperations
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.namespace.Namespace
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.*

data class GameInfo @JsonCreator constructor(
        @JsonProperty("id") val id: String,
        @JsonProperty("createdAt") val createdAt: LocalDateTime = now(),
        @JsonProperty("player1") val player1: Player,
        @JsonProperty("player2") var player2: Player? = null,
        @JsonProperty("winner") var winner: Player? = null,
        @JsonProperty("completed") var completed: Boolean = false,
        @JsonProperty("nextTurn") var nextTurn: Player = player1) {

    val joinable get() = player2 == null && !completed
}

val GameInfo.age: Duration get() = Duration.between(createdAt, now())

fun GameInfo.room(server: SocketIOServer): BroadcastOperations =
        server.getNamespace(Namespace.DEFAULT_NAME).getRoomOperations(id)