package com.nyxcode.blinded.backend.game

import com.corundumstudio.socketio.SocketIOServer
import com.nyxcode.blinded.backend.defaultNamespace
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

class Games(cleanupInterval: Duration,
            cleanupThreshold: Duration,
            private val server: SocketIOServer) {

    private val games = ConcurrentHashMap<String, Game>()

    init {
        val interval = cleanupInterval.toMillis()
        Timer().schedule(interval, interval) { cleanup(cleanupThreshold) }
    }

    private fun cleanup(threshold: Duration) {
        val iterator = games.iterator()
        while (iterator.hasNext()) {
            val game = iterator.next().value
            if (game isOlderThan threshold) {
                iterator.remove()

                val room = game.info.id
                for (client in server.defaultNamespace().getRoomOperations(room).clients) {
                    client.leaveRoom(room)
                }
            }
        }
    }

    operator fun get(id: String): Game? = games[id]

    operator fun minusAssign(id: String) {
        games.remove(id)
    }

    operator fun plusAssign(info: GameInfo) {
        assert(!games.containsKey(info.id))
        val game = Game(info = info, data = GameData())
        games[info.id] = game
    }
}