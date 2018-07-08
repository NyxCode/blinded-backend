package com.nyxcode.blinded.backend.game

import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.Config
import com.nyxcode.blinded.backend.LOG
import com.nyxcode.blinded.backend.defaultNamespace
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

class Games(config: Config, private val server: SocketIOServer) {

    private val games = ConcurrentHashMap<String, Game>()

    init {
        val interval = config.oldGamesCheckInterval.toDuration().toMillis()
        val threshold = config.oldGamesThreshold.toDuration()
        Timer().schedule(interval, interval) { cleanup(threshold) }
    }

    private fun cleanup(threshold: Duration) {
        val iterator = games.iterator()
        var removeCount = 0
        while (iterator.hasNext()) {
            val game = iterator.next().value
            if (game isOlderThan threshold || game.info.completed) {
                iterator.remove()
                removeCount++

                val room = game.info.id
                for (client in server.defaultNamespace().getRoomOperations(room).clients) {
                    client.leaveRoom(room)
                }
            }
        }
        LOG.info("Deleted $removeCount games during last cleanup")
    }

    operator fun get(id: String): Game? = games[id]

    operator fun minusAssign(game: Game) {
        games.remove(game.info.id)
    }

    operator fun plusAssign(info: GameInfo) {
        assert(!games.containsKey(info.id))
        val game = Game(info = info, data = GameData())
        games[info.id] = game
    }
}