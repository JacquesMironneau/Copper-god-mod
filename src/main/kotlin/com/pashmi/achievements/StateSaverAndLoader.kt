package com.pashmi.achievements

import com.pashmi.CopperGodMod.MOD_ID
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.MinecraftServer
import net.minecraft.world.PersistentState
import net.minecraft.world.World
import java.util.*

data class PlayerData(var copperOreBroken: Int = 0)

data class PlayerHome(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0)

fun PlayerHome.isDefault() = x == 0.0 && y == 0.0 && z == 0.0


private const val PLAYER_HOMES_TAG = "playerHomes"
private const val COPPER_BROKEN_TAG = "copperBroken"
private const val TOTAL_COPPER_ORE_BROKEN_TAG = "totalCopperOreBroken"
private const val PLAYERS_TAG = "players"
private const val VEC3D_X_TAG = "x"
private const val VEC3D_Y_TAG = "y"
private const val VEC3D_Z_TAG = "z"

class StateSaverAndLoader : PersistentState() {

    var totalCopperOreBroken = 0

    val players = mutableMapOf<UUID, PlayerData>()

    val playerHomes = mutableMapOf<UUID, PlayerHome>()

    companion object {


        private var type: Type<StateSaverAndLoader> = Type<StateSaverAndLoader>(
            { StateSaverAndLoader() }, StateSaverAndLoader::createFromNbt, null
        )

        private fun createFromNbt(tag: NbtCompound): StateSaverAndLoader {
            val state = StateSaverAndLoader()
            state.totalCopperOreBroken = tag.getInt(TOTAL_COPPER_ORE_BROKEN_TAG)
            val players: NbtCompound = tag.getCompound(PLAYERS_TAG)
            players.keys.forEach { key ->
                val data = PlayerData()
                data.copperOreBroken = players.getCompound(key).getInt(COPPER_BROKEN_TAG)

                val uuid = UUID.fromString(key)
                state.players[uuid] = data
            }

            val playersHome: NbtCompound = tag.getCompound(PLAYER_HOMES_TAG)
            playersHome.keys.forEach {
                val nbtHome = playersHome.getCompound(it)
                state.playerHomes[UUID.fromString(it)] = PlayerHome(
                    nbtHome.getDouble(VEC3D_X_TAG), nbtHome.getDouble(VEC3D_Y_TAG), nbtHome.getDouble(
                        VEC3D_Z_TAG
                    )
                )
            }

            return state
        }

        private fun getServerState(server: MinecraftServer): StateSaverAndLoader {
            val persistentStateManager = server.getWorld(World.OVERWORLD)!!.persistentStateManager
            return persistentStateManager.getOrCreate<StateSaverAndLoader>(type, MOD_ID).also { it.markDirty() }
        }

        fun getPlayerState(player: LivingEntity): PlayerData {
            val serverState = player.world.server?.let { getServerState(it) }
            val playerState = serverState?.players?.getOrPut(player.uuid) { PlayerData() }
            return playerState ?: PlayerData()
        }

        fun getPlayerHome(player: LivingEntity): PlayerHome {
            val serverState = player.world.server?.let { getServerState(it) }
            val playerState = serverState?.playerHomes?.getOrPut(player.uuid) { PlayerHome() }
            return playerState ?: PlayerHome()
        }
    }


    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        nbt.putInt(TOTAL_COPPER_ORE_BROKEN_TAG, totalCopperOreBroken)

        print(players)
        val playersNbt = NbtCompound()
        players.forEach { (uuid, data) ->
            val playerNbt = NbtCompound().apply {
                putInt(COPPER_BROKEN_TAG, data.copperOreBroken)
            }
            playersNbt.put(uuid.toString(), playerNbt)
        }
        nbt.put(PLAYERS_TAG, playersNbt)

        val playerHomesNbt = NbtCompound()
        playerHomes.forEach { (uuid, data) ->
            val playerHomeNbt = NbtCompound().apply {
                putDouble(VEC3D_X_TAG, data.x)
                putDouble(VEC3D_Y_TAG, data.y)
                putDouble(VEC3D_Z_TAG, data.z)
            }
            playerHomesNbt.put("$uuid", playerHomeNbt)
        }

        nbt.put(PLAYER_HOMES_TAG, playerHomesNbt)

        return nbt
    }

}