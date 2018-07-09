package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.namespace.Namespace
import com.nyxcode.blinded.backend.game.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.reflect.KProperty0

val LOG: Logger = LoggerFactory.getLogger("default")

private val CHARS = ('0'..'9') + ('a'..'z') + ('A'..'Z')

fun randomString(len: Int): String = StringBuilder().apply {
    val random = ThreadLocalRandom.current()
    for (i in 0..len) {
        val index = random.nextInt(CHARS.size)
        val char = CHARS[index]
        append(char)
    }
}.toString()


inline fun <reified O> SocketIOServer.addEventListener(eventName: String, listener: DataListener<O>) =
        addEventListener(eventName, O::class.java, listener)

fun SocketIOServer.defaultNamespace(): SocketIONamespace = this.getNamespace(Namespace.DEFAULT_NAME)

fun createProperties(vararg entries: KProperty0<*>) = Properties().apply {
    for (entry in entries) {
        val key = entry.name
        val value = entry.get().toString()
        setProperty(key, value)
    }
}

fun loadProperties(file: File) = Properties().apply { file.reader().use { load(it) } }

fun Properties.store(file: File) = file.writer().use { store(it, "") }

fun newPlayer(config: Config): Player = randomString(config.playerKeyLen)

fun newGameID(config: Config): String = randomString(config.gameKeyLen)

fun now(): LocalDateTime = LocalDateTime.now()

fun today(): LocalDate = LocalDate.now()

fun allEqual(vararg args: Any?, mayBeNull: Boolean = true): Boolean {
    return if (args.isEmpty()) {
        true
    } else {
        val reference = args.first()
        if (!mayBeNull && reference == null) return false
        args.none { it != reference }
    }
}