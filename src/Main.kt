package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.game.*
import game.*

fun main(args: Array<String>) {
    val config = Config.loadOrCreate("config.properties").also(::println)
    val server = createSocketIOServer(config)
    val stats = Statistics()
    val games = Games(config, server, stats)

    server.registerListener(games, config, stats)
    server.start()
}

fun createSocketIOServer(config: Config): SocketIOServer {
    val configuration = Configuration().apply {
        hostname = config.hostname
        port = config.port
    }
    return SocketIOServer(configuration)
}

fun SocketIOServer.registerListener(games: Games, config: Config, stats: Statistics) {
    addEventListener(CreateGame.NAME, CreateGameListener(games, config))
    addEventListener(JoinGame.NAME, JoinGameListener(games, this, config))
    addEventListener(RequestBot.NAME, RequestBotListener(games))
    addEventListener(DoTurn.NAME, DoTurnListener(games, this))
    addEventListener(RequestStatistics.NAME, RequestStatisticsListener(stats))
}