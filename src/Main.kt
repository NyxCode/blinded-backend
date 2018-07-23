package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.Configuration
import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.game.*
import game.*
import io.javalin.Javalin
import io.javalin.embeddedserver.Location
import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val config = Config("blinded.cfg").also(Config::save)
    val stats = Statistics()
    val timer = Timer()
    val socketIO = createSocketIO(config)
    val games = Games(config, timer, socketIO, stats)
    val server = createJavalin(config)

    server.init(config)
    socketIO.init(games, config, stats)

}

fun createSocketIO(config: Config) =
        SocketIOServer(Configuration().apply {
            // hostname = config.hostname
            port = config.apiPort
        })

fun createJavalin(config: Config): Javalin = Javalin
        .create()
        .disableStartupBanner()
        .enableDynamicGzip()
        .enableStandardRequestLogging()
        .port(config.serverPort)

fun Javalin.init(config: Config) {
    val dir = config.frontendDirectory
    val path = dir.path
    LOG.info("initializing frontend ({})", path)
    enableStaticFiles(path, Location.EXTERNAL)

    val index = File(dir, "index.html").readText()
    val game = File(dir, "game.html").readText()
    val joinGame = File(dir, "join-game.html").readText()
    val multiPlayer = File(dir, "multiplayer.html").readText()
    val play = File(dir, "play.html").readText()
    val result = File(dir, "result.html").readText()

    get("/") { it.resultHTML(index) }
    get("/game/") { it.resultHTML(game) }
    get("/join-game/") { it.resultHTML(joinGame) }
    get("/multiplayer/") { it.resultHTML(multiPlayer) }
    get("/play/") { it.resultHTML(play) }
    get("/result/") { it.resultHTML(result) }

    disableStartupBanner()
    port(config.serverPort)
    enableCorsForOrigin(config.hostname + ":" + config.apiPort)
    start()
}

fun SocketIOServer.init(games: Games, config: Config, stats: Statistics) {
    addEventListener(CreateGame.NAME, CreateGameListener(games, config))
    addEventListener(JoinGame.NAME, JoinGameListener(games, this, config))
    addEventListener(RequestBot.NAME, RequestBotListener(games))
    addEventListener(DoTurn.NAME, DoTurnListener(games, this))
    addEventListener(RequestStatistics.NAME, RequestStatisticsListener(stats))
    start()
}
