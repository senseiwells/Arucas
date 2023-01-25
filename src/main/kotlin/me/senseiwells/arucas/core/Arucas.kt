package me.senseiwells.arucas.core

import java.nio.file.Path

object Arucas {
    @JvmStatic
    val VERSION = "2.1.0"
    @JvmStatic
    val PATH: Path = Path.of(System.getProperty("user.home")).resolve(".arucas")
}