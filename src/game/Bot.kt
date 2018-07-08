package com.nyxcode.blinded.backend.game

import kotlin.math.max
import kotlin.math.min

data class Move(val x: Int, val y: Int)


class Bot(game: Game) {

    init {
        require(game.info.player2 == Bot.ID)
    }

    private val player = game.info.player2
    private val opponent = game.info.player1
    private val board = game.data.board

    companion object {
        const val ID = "AN-INTELLIGENT-BOT"
    }

    private fun eval() = when (board.winner) {
        player -> 10
        opponent -> -10
        else -> 0
    }

    private fun minimax(depth: Int, isMax: Boolean): Int {
        val score = eval()

        if (score == 10 || score == -10) return score
        if (board.full) return 0

        return if (isMax) {
            var best = Int.MIN_VALUE
            board.forEachCell { x, y, cell ->
                if (cell == null) {
                    board[x][y] = player
                    best = max(best, minimax(depth + 1, !isMax))
                    board[x][y] = null
                }
            }
            best
        } else {
            var best = Int.MAX_VALUE
            board.forEachCell { x, y, cell ->
                if (cell == null) {
                    board[x][y] = opponent
                    best = min(best, minimax(depth + 1, !isMax))
                    board[x][y] = null
                }
            }
            best
        }
    }

    fun findBestMove(): Move {
        var bestVal = -1000
        var bestMove: Move? = null

        board.forEachCell { x, y, cell ->
            if (cell == null) {
                board[x][y] = player
                val moveVal = minimax(0, false)
                board[x][y] = null
                if (moveVal > bestVal) {
                    bestMove = Move(x, y)
                    bestVal = moveVal
                }
            }
        }

        return checkNotNull(bestMove)
    }
}