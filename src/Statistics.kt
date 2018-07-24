package com.nyxcode.blinded.backend.game

import com.nyxcode.blinded.backend.LOG
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime.now
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Statistics {
    var gamesToday: Int = 0
    var runningGames: Int = 0

    init {
        val midnight = LocalDate.now().plusDays(1).atStartOfDay()
        val delay = Duration.between(now(), midnight).toMillis()
        val interval = TimeUnit.DAYS.toMillis(1)

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay({
            LOG.info("Daily game counter set to 0")
            gamesToday = 0
        }, delay, interval, TimeUnit.MILLISECONDS)
    }

    override fun toString() = "Statistics(gamesToday=$gamesToday, runningGames=$runningGames)"
}
