package com.nyxcode.blinded.backend.game

typealias GameBoard = Array<Array<Player?>>

inline fun GameBoard.forEachCell(block: (x: Int, y: Int, cell: Player?) -> Unit) {
    for (x in 0..2) for (y in 0..2) block(x, y, this[x][y])
}

val GameBoard.full: Boolean
    get() {
        var full = true
        forEachCell { _, _, cell -> if (cell == null) full = false }
        return full
    }

val GameBoard.winner: Player?
    get() {
        // check rows and columns
        for (i in 0..2) {
            when {
                eqNotNull(this[0][i], this[1][i], this[2][i]) -> return this[0][i]
                eqNotNull(this[i][0], this[i][1], this[i][2]) -> return this[i][0]
            }
        }
        // check diagonal or if the board is full
        return when {
            eqNotNull(this[0][0], this[1][1], this[2][2]) -> this[0][0]
            eqNotNull(this[2][0], this[1][1], this[0][2]) -> this[2][0]
            else -> null
        }
    }

private fun eqNotNull(p1: Player?, p2: Player?, p3: Player?) = p1 != null && p1 == p2 && p2 == p3

fun newEmptyGameboard(): GameBoard = arrayOf(
        Array<Player?>(3) { null },
        Array<Player?>(3) { null },
        Array<Player?>(3) { null })



