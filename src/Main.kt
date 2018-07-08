package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.game.*
import game.CreateGameListener
import game.DoTurnListener
import game.JoinGameListener
import game.RequestBotListener
import java.io.File
import java.time.Duration
import java.util.*

fun main(args: Array<String>) {
    val props = Properties()
    File("config.properties").reader().use {
        props.load(it)
    }

    val config = Configuration()
    config.hostname = props.getProperty("hostname")
    config.port = props.getProperty("port").toInt()

    val server = SocketIOServer(config)
    val games = Games(
            cleanupInterval = Duration.ofMinutes(10),
            cleanupThreshold = Duration.ofMinutes(10),
            server = server)

    server.addEventListener(CreateGame.NAME, CreateGameListener(games))

    server.addEventListener(JoinGame.NAME, JoinGameListener(games, server))

    server.addEventListener(RequestBot.NAME, RequestBotListener(games))

    server.addEventListener(DoTurn.NAME, DoTurnListener(games, server))

    server.start()
}

