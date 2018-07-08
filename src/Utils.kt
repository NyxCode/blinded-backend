package com.nyxcode.blinded.backend

import com.corundumstudio.socketio.SocketIONamespace
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.namespace.Namespace
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.concurrent.ThreadLocalRandom

val LOG: Logger = LoggerFactory.getLogger("default")

private val CHARS = ('0'..'9') + ('a'..'z') + ('A'..'Z')

const val KEY_LEN = 4

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

fun now(): LocalDateTime = LocalDateTime.now()

fun SocketIOServer.defaultNamespace(): SocketIONamespace = this.getNamespace(Namespace.DEFAULT_NAME)
