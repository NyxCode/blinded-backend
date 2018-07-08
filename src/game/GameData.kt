package com.nyxcode.blinded.backend.game

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class GameData @JsonCreator constructor(@JsonProperty("board") val board: GameBoard = arrayOf(
        Array<Player?>(3) { null },
        Array<Player?>(3) { null },
        Array<Player?>(3) { null })) {
}

