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
                  val oldGamesCheckInterval: TimeSpan = TimeSpan(10, MINUTES),
                  val oldGamesThreshold: TimeSpan = TimeSpan(10, MINUTES)) {


    constructor(props: Properties) : this(
            hostname = props.getProperty(Config::hostname.name),
            port = props.getProperty(Config::port.name).toInt(),
            gameKeyLen = props.getProperty(Config::gameKeyLen.name).toInt(),
            playerKeyLen = props.getProperty(Config::playerKeyLen.name).toInt(),
            oldGamesCheckInterval = TimeSpan.fromString(props.getProperty(Config::oldGamesCheckInterval.name)),
            oldGamesThreshold = TimeSpan.fromString(props.getProperty(Config::oldGamesThreshold.name)))

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
            ::oldGamesCheckInterval, ::oldGamesThreshold)
}

data class TimeSpan(val amount: Long, val unit: ChronoUnit) {

    companion object {
        fun fromString(str: String): TimeSpan {
            val splitted = str.split(' ')
            val amount = splitted[0].toLong()
            val unit = splitted[1].toUpperCase()
            return TimeSpan(amount, ChronoUnit.valueOf(unit))
        }
    }

    fun toDuration(): Duration = Duration.of(amount, unit)
    override fun toString() = "$amount ${unit.toString().toUpperCase()}"
}