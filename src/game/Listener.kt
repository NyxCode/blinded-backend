package game

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.listener.DataListener
import com.nyxcode.blinded.backend.*
import com.nyxcode.blinded.backend.game.*
import java.util.*
import kotlin.concurrent.schedule

class CreateGameListener(private val games: Games,
                         private val config: Config) : DataListener<CreateGame> {
    override fun onData(client: SocketIOClient, data: CreateGame, req: AckRequest) {
        LOG.info("Rec: CreateGame")
        val player = randomString(config.playerKeyLen)
        val game = GameInfo(id = newGameID(config), player1 = player)
        games += game
        client.joinRoom(game.id)
        req.sendAckData(game)
    }
}

class JoinGameListener(private val games: Games,
                       private val server: SocketIOServer,
                       private val config: Config) : DataListener<JoinGame> {
    override fun onData(client: SocketIOClient, data: JoinGame, req: AckRequest) {
        LOG.info("Rec: JoinGame")
        val game = games[data.id]?.info
        if (game == null) {
            req.sendAckData(Error("We couldn't not find the requested game"))
            return
        }

        when {
            !game.joinable -> req.sendAckData(Error("You can't join this game right now"))
            else -> {
                val player2 = randomString(config.playerKeyLen)
                game.player2 = player2
                server.defaultNamespace()
                        .getRoomOperations(game.id)
                        .clients
                        .first()
                        .sendEvent(PlayerJoined.NAME, PlayerJoined(player2))
                client.joinRoom(game.id)
                req.sendAckData(game)

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
                req.sendAckData(Error("Waiting for an other player to join..."))
                return
            }
            else -> {
                req.sendAckData(Error("You can't do that!"))
                return
            }
        }
        val room = server.defaultNamespace().getRoomOperations(game.info.id)
        val otherClient = if (otherPlayer == Bot.ID) null else room.clients.first { it != client }

        when {
        // When the player is not next
            game.info.nextTurn != player ->
                req.sendAckData(Error("It's not your turn!"))

        // When the given coordinates are invalid
            data.x !in 0..3 || data.y !in 0..3 ->
                req.sendAckData(Error("Field coordinates out of range!"))

        // When the field is already full
            game.data.board[data.x][data.y] != null -> {
                game.info.completed = true
                game.info.winner = otherPlayer
                client.sendEvent(Disqualified.NAME, Disqualified("Whoops, you are disqualified", game))
                otherClient?.sendEvent(GameCompleted.NAME, GameCompleted(game))
            }

            else -> {
                game.data.board[data.x][data.y] = player
                game.info.nextTurn = otherPlayer
                req.sendAckData(game.info)

                game.updateState()
                if (game.info.completed) {
                    room.sendEvent(GameCompleted.NAME, GameCompleted(game))
                    games -= game
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
        val bot = Bot(Bot.ID, game.info.player1)
        val move = bot.findBestMove(game.data.board)
        game.data.board[move.x][move.y] = Bot.ID
        game.info.nextTurn = game.info.player1
        game.updateState()

        if (game.info.completed) {
            playerClient.sendEvent(GameCompleted.NAME, GameCompleted(game))
        } else {
            playerClient.sendEvent(EnemyTurn.NAME, EnemyTurn(move.x, move.y))
        }
    }
}

class RequestBotListener(private val games: Games) : DataListener<RequestBot> {
    override fun onData(client: SocketIOClient, data: RequestBot, ack: AckRequest) {
        LOG.info("req bot")
        val game = games[data.id] ?: run {
            ack.sendAckData(Error.GAME_NOT_FOUND)
            return
        }
        if (game.info.player2 != null) {
            ack.sendAckData(Error("There is already an other player playing in this game!"))
            return
        }

        val botID = Bot.ID
        game.info.player2 = botID
        ack.sendAckData(PlayerJoined(botID))
        Bot(botID, game.info.player1)
    }
}