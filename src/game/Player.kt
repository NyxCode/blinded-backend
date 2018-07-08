package com.nyxcode.blinded.backend.game

import com.nyxcode.blinded.backend.KEY_LEN
import com.nyxcode.blinded.backend.randomString

typealias Player = String

fun newPlayer(): Player = randomString(KEY_LEN)