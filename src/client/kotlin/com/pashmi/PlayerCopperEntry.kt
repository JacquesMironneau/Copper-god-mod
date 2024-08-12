package com.pashmi

import java.util.*

data class PlayerCopperRegistry(val registry: MutableMap<UUID, Int> = mutableMapOf())

fun PlayerCopperRegistry.put(uuid: UUID, copperCount: Int) {
    registry[uuid] = copperCount
}
fun PlayerCopperRegistry.get(uuid: UUID) = registry[uuid]
