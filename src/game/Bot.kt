package com.nyxcode.blinded.backend.game

import kotlin.math.max
import kotlin.math.min

data class Move(val x: Int, val y: Int)


class Bot(val player: Player, val opponent: Player) {

    companion object {
        const val ID = "AN-INTELLIGENT-BOT"
    }

    fun eval(board: GameBoard) = when (board.winner) {
        player -> 10
        opponent -> -10
        else -> 0
    }

    fun minimax(board: GameBoard, depth: Int, isMax: Boolean): Int {
        val score = eval(board)

        if (score == 10 || score == -10) return score
        if (board.full) return 0

        return if (isMax) {
            var best = -1000
            board.forEachCell { x, y, cell ->
                if (cell == null) {
                    board[x][y] = player
                    best = max(best, minimax(board, depth + 1, !isMax))
                    board[x][y] = null
                }
            }
            best
        } else {
            var best = 1000
            board.forEachCell { x, y, cell ->
                if (cell == null) {
                    board[x][y] = opponent
                    best = min(best, minimax(board, depth + 1, !isMax))
                    board[x][y] = null
                }
            }
            best
        }
    }

    fun findBestMove(board: GameBoard): Move {
        var bestVal = -1000
        var bestMove: Move? = null

        board.forEachCell { x, y, cell ->
            if(cell == null) {
                board[x][y] = player
                val moveVal = minimax(board, 0, false)
                board[x][y] = null
                if(moveVal > bestVal) {
                    bestMove = Move(x, y)
                    bestVal = moveVal
                }
            }
        }

        return checkNotNull(bestMove)
    }
}