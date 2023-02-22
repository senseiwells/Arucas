package me.senseiwells.arucas.core

import java.nio.file.Path

object Arucas {
    const val VERSION = "2.2.0"
    @JvmField
    val PATH: Path = Path.of(System.getProperty("user.home")).resolve(".arucas")
}