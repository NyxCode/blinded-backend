package game

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import com.nyxcode.blinded.backend.Config
import com.nyxcode.blinded.backend.game.*
import com.nyxcode.blinded.backend.newGameID
import com.nyxcode.blinded.backend.newPlayer
import com.nyxcode.blinded.backend.randomString
import java.util.*
import kotlin.concurrent.schedule

class CreateGameListener(private val games: Games, private val config: Config) : DataListener<CreateGame> {
    override fun onData(client: SocketIOClient, data: CreateGame, req: AckRequest) {
        val player = randomString(config.playerKeyLen)
        val gameInfo = GameInfo(id = newGameID(config), player1 = player)
        games.register(Game(gameInfo))
        client.joinRoom(gameInfo.id)
        req.sendAckData(gameInfo)
    }
}

class JoinGameListener(private val games: Games,
                       private val server: SocketIOServer,
                       private val config: Config) : DataListener<JoinGame> {
    override fun onData(client: SocketIOClient, data: JoinGame, req: AckRequest) {
        val info = games[data.id]?.info ?: run {
            req.sendAckData(Error.GAME_NOT_FOUND)
            return
        }

        when {
            info.completed || info.player2 == null -> req.sendAckData(Error.CANT_JOIN)
            else -> {
                val player2 = newPlayer(config)
                info.player2 = player2
                info.room(server).clients.first().sendEvent(PlayerJoined.NAME, PlayerJoined(info.id, player2))
                client.joinRoom(info.id)
                req.sendAckData(info)
            }
        }
    }
}

class DoTurnListener(private val games: Games, private val server: SocketIOServer) : DataListener<DoTurn> {
    private val timer = Timer()

    override fun onData(client: SocketIOClient, data: DoTurn, req: AckRequest) {
        val player = data.player
        if (player == Bot.ID) return

        val game = games[data.gameID] ?: run {
            req.sendAckData(Error.GAME_NOT_FOUND)
            return
        }
        val otherPlayer = when (data.player) {
            game.info.player2 -> game.info.player1
            game.info.player1 -> game.info.player2 ?: run {
                req.sendAckData(Error.WAITING_FOR_PLAYER)
                return
            }
            else -> {
                req.sendAckData(Error.UNEXPECTED)
                return
            }
        }
        val room = game.room(server)
        val otherClient = if (otherPlayer == Bot.ID) null else room.clients.first { it != client }

        when {
        // When the player is not next
            game.info.nextTurn != player ->
                req.sendAckData(Error.NOT_YOUR_TURN)

        // When the given coordinates are invalid
            data.x !in 0..3 || data.y !in 0..3 ->
                req.sendAckData(Error.UNEXPECTED)

        // When the field is already full
            game.board[data.x][data.y] != null -> {
                game.info.completed = true
                game.info.winner = otherPlayer
                client.sendEvent(Disqualified.NAME, Disqualified("Whoops, you are disqualified", game))
                otherClient?.sendEvent(GameCompleted.NAME, GameCompleted(game))
                games.unregister(game.id)
            }

            else -> {
                game.board[data.x][data.y] = player
                game.info.nextTurn = otherPlayer
                req.sendAckData(game.info)

                game.updateState()
                if (game.info.completed) {
                    room.sendEvent(GameCompleted.NAME, GameCompleted(game))
                    games.unregister(game.id)
                } else {
                    otherClient?.sendEvent(EnemyTurn.NAME, EnemyTurn(data.x, data.y))
                    if (otherPlayer == Bot.ID) {
                        timer.schedule(2000) { doBotTurn(client, game) }
                    }
                }
            }
        }
    }

    private fun doBotTurn(playerClient: SocketIOClient, game: Game) {
        check(game.info.player2 == Bot.ID)
        val move = Bot(game).findBestMove()
        game.board[move.x][move.y] = Bot.ID
        game.info.nextTurn = game.info.player1
        game.updateState()

        if (game.info.completed) {
            playerClient.sendEvent(GameCompleted.NAME, GameCompleted(game))
            games.unregister(game.id)
        } else {
            playerClient.sendEvent(EnemyTurn.NAME, EnemyTurn(move.x, move.y))
        }
    }
}

class RequestBotListener(private val games: Games) : DataListener<RequestBot> {
    override fun onData(client: SocketIOClient, data: RequestBot, ack: AckRequest) {
        val game = games[data.id] ?: run {
            ack.sendAckData(Error.GAME_NOT_FOUND)
            return
        }
        if (game.info.player2 != null) {
            ack.sendAckData(Error.CANT_JOIN)
            return
        }

        game.info.player2 = Bot.ID
        ack.sendAckData(PlayerJoined(game.id, Bot.ID))
    }
}

class RequestStatisticsListener(val stats: Statistics) : DataListener<RequestStatistics> {
    override fun onData(client: SocketIOClient, data: RequestStatistics, ack: AckRequest) {
        ack.sendAckData(stats)
    }
}