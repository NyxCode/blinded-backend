package com.nyxcode.blinded.backend

import java.io.File
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.MINUTES
import java.util.*

data class Config(val hostname: String = "localhost",
                  val port: Int = 9999,
                  val gameKeyLen: Int = 4,
                  val playerKeyLen: Int = 128,
                  val removeOldGamesCheckInterval: TimeSpan = TimeSpan(10, MINUTES),
                  val removeOldGamesThreshold: TimeSpan = TimeSpan(10, MINUTES)) {

    data class TimeSpan(val amount: Long, val unit: ChronoUnit) {
        constructor(string: String) : this(string.split(" ")[0].toLong(), ChronoUnit.valueOf(string.split(" ")[1]))

        fun toDuration(): Duration = Duration.of(amount, unit)
        override fun toString() = "$amount ${unit.toString().toUpperCase()}"
    }

    constructor(props: Properties) : this(
            hostname = props.getProperty(Config::hostname.name),
            port = props.getProperty(Config::port.name).toInt(),
            gameKeyLen = props.getProperty(Config::gameKeyLen.name).toInt(),
            playerKeyLen = props.getProperty(Config::playerKeyLen.name).toInt(),
            removeOldGamesCheckInterval = TimeSpan(props.getProperty(Config::removeOldGamesCheckInterval.name)),
            removeOldGamesThreshold = TimeSpan(props.getProperty(Config::removeOldGamesThreshold.name)))

    init {
        require(hostname.isNotEmpty())
        require(port in 0..65535)
        require(gameKeyLen > 0)
        require(playerKeyLen > 0)
    }

    companion object {
        fun loadOrCreate(path: String): Config {
            val file = File(path)
            return if (file.exists()) Config(loadProperties(file))
            else Config().also { it.toProperties().store(file) }
        }
    }

    fun toProperties() = createProperties(::hostname, ::port, ::gameKeyLen, ::playerKeyLen,
            ::removeOldGamesCheckInterval, ::removeOldGamesThreshold)
}
