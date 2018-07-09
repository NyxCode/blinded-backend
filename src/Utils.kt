package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.namespace.Namespace
import com.nyxcode.blinded.backend.game.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

val LOG: Logger = LoggerFactory.getLogger("default")

// only using characters which can't be confused with each other
private const val CHARS = "abdefghikmnprstwxyABDEFGHKLMNPRSTY23456789"

fun randomString(len: Int): String = StringBuilder().apply {
    val random = ThreadLocalRandom.current()
    for (i in 0..len) {
        val index = random.nextInt(CHARS.length)
        val char = CHARS[index]
        append(char)
    }
}.toString()


inline fun <reified O> SocketIOServer.addEventListener(eventName: String, listener: DataListener<O>) =
        addEventListener(eventName, O::class.java, listener)

val SocketIOServer.defNSpace: SocketIONamespace get() = this.getNamespace(Namespace.DEFAULT_NAME)

fun newPlayer(config: Config): Player = randomString(config.playerKeyLen)

fun newGameID(config: Config): String = randomString(config.gameKeyLen)

fun now(): LocalDateTime = LocalDateTime.now()

fun allEqual(vararg args: Any?, mayBeNull: Boolean = true): Boolean {
    return if (args.isEmpty()) {
        true
    } else {
        val reference = args.first()
        if (!mayBeNull && reference == null) return false
        args.none { it != reference }
    }
}
