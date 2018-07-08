package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.game.*
import game.CreateGameListener
import game.DoTurnListener
import game.JoinGameListener
import game.RequestBotListener

fun main(args: Array<String>) {
    val config = Config.loadOrCreate("config.properties").also(::println)
    val server = createSocketIOServer(config)
    val games = Games(config, server)

    server.registerListener(games, config)
    server.start()
}

fun createSocketIOServer(config: Config): SocketIOServer {
    val configuration = Configuration().apply {
        hostname = config.hostname
        port = config.port
    }
    return SocketIOServer(configuration)
}

fun SocketIOServer.registerListener(games: Games, config: Config) {
    addEventListener(CreateGame.NAME, CreateGameListener(games, config))
    addEventListener(JoinGame.NAME, JoinGameListener(games, this, config))
    addEventListener(RequestBot.NAME, RequestBotListener(games))
    addEventListener(DoTurn.NAME, DoTurnListener(games, this))
}