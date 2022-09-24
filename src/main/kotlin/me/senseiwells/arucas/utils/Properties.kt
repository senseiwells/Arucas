package me.senseiwells.arucas.utils

import java.util.*

class Properties(
    var isDebug: Boolean = false,
    var isExperimental: Boolean = false,
    var logDeprecated: Boolean = false,
    var errorMaxLength: Int = 60,
    var id: UUID = UUID.randomUUID()
)