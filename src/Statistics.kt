package com.nyxcode.blinded.backend.game

import com.nyxcode.blinded.backend.now
import com.nyxcode.blinded.backend.today
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Statistics {
    var gamesToday: Int = 0
    var runningGames: Int = 0

    init {
        val midnight = today().plusDays(1).atStartOfDay()
        val delay = Duration.between(now(), midnight).toMillis()

        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay({
            gamesToday = 0
        }, delay, 1, TimeUnit.DAYS)
    }
}
