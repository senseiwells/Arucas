package me.senseiwells.arucas.utils

import java.util.UUID

class Properties(
    var isDebug: Boolean = false,
    var isExperimental: Boolean = false,
    var logDeprecated: Boolean = false,
    var id: UUID = UUID.randomUUID()
)