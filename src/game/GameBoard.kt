package com.nyxcode.blinded.backend.game

import com.nyxcode.blinded.backend.allEqual

val EmptyCell: Cell = null
typealias Cell = Player?
typealias Column = Array<Cell>
typealias GameBoard = Array<Column>

inline fun GameBoard.forEachCell(block: (x: Int, y: Int, cell: Cell) -> Unit) {
    for (x in 0..2)
        for (y in 0..2)
            block(x, y, this[x][y])
}

val GameBoard.full: Boolean
    get() {
        var full = true
        forEachCell { _, _, cell -> full = cell == EmptyCell }
        return full
    }

val GameBoard.winner: Player?
    get() {
        // check rows and columns
        for (i in 0..2) {
            when {
                allEqual(this[0][i], this[1][i], this[2][i], mayBeNull = false) -> return this[0][i]
                allEqual(this[i][0], this[i][1], this[i][2], mayBeNull = false) -> return this[i][0]
            }
        }
        // check diagonal or if the board is full
        return when {
            allEqual(this[0][0], this[1][1], this[2][2], mayBeNull = false) -> this[0][0]
            allEqual(this[2][0], this[1][1], this[0][2], mayBeNull = false) -> this[2][0]
            else -> null
        }
    }

fun newBoard() = GameBoard(3) { Column(3) { EmptyCell } }




