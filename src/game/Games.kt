package com.nyxcode.blinded.backend.game

import com.nyxcode.blinded.backend.Config
import com.nyxcode.blinded.backend.LOG
import java.time.Duration
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

class Games(cfg: Config,
            timer: Timer,
            private val statistics: Statistics) {

    private val games = ConcurrentHashMap<String, Game>()

    init {
        val rate = cfg.checkRate
        val timeout = cfg.timeout
        LOG.info("Deleting games older than $timeout every $rate")

        val interval = rate.toMillis()
        timer.schedule(interval, interval) { cleanup(timeout) }
    }

    private fun cleanup(threshold: Duration) {
        var removed = 0

        games.entries.removeIf {
            if (it.value.age > threshold) {
                removed++
                true
            } else {
                false
            }
        }

        if (removed > 0) LOG.info("Removed $removed games")
    }

    private fun updateStats(newGame: Boolean = false) = with(statistics) {
        if (newGame) gamesToday++
        runningGames = games.size
    }

    operator fun get(id: String): Game? = games[id]

    fun unregister(id: String): Game? = games.remove(id).also { updateStats() }

    fun register(game: Game) {
        check(!games.containsKey(game.id))
        games[game.id] = game
        updateStats(newGame = true)
    }
}