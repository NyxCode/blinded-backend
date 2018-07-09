package com.nyxcode.blinded.backend.game

import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.Config
import com.nyxcode.blinded.backend.LOG
import com.nyxcode.blinded.backend.defaultNamespace
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

class Games(config: Config,
            private val server: SocketIOServer,
            private val statistics: Statistics) {

    private val games = ConcurrentHashMap<String, Game>()

    init {
        val interval = config.oldGamesCheckInterval.toDuration()
        val threshold = config.oldGamesThreshold.toDuration()
        LOG.info("Deleting games older than $threshold every $interval")

        val intervalMillis = interval.toMillis()
        Timer().schedule(intervalMillis, intervalMillis) { cleanup(threshold) }
    }

    private fun cleanup(threshold: Duration) {
        val iterator = games.iterator()
        var removeCount = 0
        while (iterator.hasNext()) {
            val game = iterator.next().value
            if (game.age >= threshold || game.info.completed) {
                iterator.remove()
                removeCount++

                val room = game.info.id
                for (client in server.defaultNamespace().getRoomOperations(room).clients) {
                    client.leaveRoom(room)
                }
            }
        }
        if (removeCount > 0)
            LOG.info("Deleted $removeCount games during last cleanup")
    }

    private fun updateStats(newGame: Boolean = false) = with(statistics) {
        if (newGame) gamesToday++
        runningGames = games.size
    }

    operator fun get(id: String): Game? = games[id]
    fun unregister(id: String): Game? = games.remove(id).also { updateStats() }

    fun unregister(game: Game): Game? = unregister(game.id)

    fun registered(id: String): Boolean = games.containsKey(id)
    fun registered(game: Game): Boolean = registered(game.id)

    fun register(game: Game) {
        check(!registered(game))
        games[game.id] = game
        updateStats(newGame = true)
    }

    fun register(gameInfo: GameInfo): Game = Game(gameInfo).also { register(it) }
}