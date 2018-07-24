package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import com.nyxcode.blinded.backend.game.Player
import io.javalin.Context
import io.javalin.core.util.Header
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ThreadLocalRandom

val LOG: Logger = LoggerFactory.getLogger("default")

// only using characters which can't be confused with each other
private const val CHARS = "abdefghikmnprstwxyABDEFGHKLMNPRSTY23456789"

private fun randomString(len: Int): String = StringBuilder().apply {
    val random = ThreadLocalRandom.current()
    for (i in 0..len) {
        val index = random.nextInt(CHARS.length)
        val char = CHARS[index]
        append(char)
    }
}.toString()


inline fun <reified O> SocketIOServer.addEventListener(eventName: String, listener: DataListener<O>) =
        addEventListener(eventName, O::class.java, listener)

fun newPlayer(config: Config): Player = randomString(config.playerKeyLen)

fun newGameID(config: Config): String = randomString(config.gameKeyLen)

fun allEqual(vararg args: Any?, mayBeNull: Boolean = true) =
        if (args.isEmpty()) {
            true
        } else {
            val reference = args.first()
            when {
                !mayBeNull && reference == null -> false
                else -> args.none { it != reference }
            }
        }


fun Context.resultHTML(html: String) {
    contentType("text/html")
    header(Header.CACHE_CONTROL, "no-cache")
    header(Header.PRAGMA, "no-cache")
    result(html)
}