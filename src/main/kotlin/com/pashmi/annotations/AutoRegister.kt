package com.pashmi.annotations

import com.pashmi.CopperGodMod.toModId
import com.pashmi.utils.logger
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredMembers

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class AutoRegister(val id: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterClass(val modId: String)

class RegisterManager {

    companion object {
        private val logger = logger()

        private val itemsToRegister = mutableMapOf<Identifier, Item>()
        private val effectsToRegister = mutableMapOf<Identifier, StatusEffect>()
        private val blocksToRegister = mutableMapOf<Identifier, Block>()


        private fun register(itemId: Identifier, item: Item): Item =
            Registry.register(Registries.ITEM, itemId, item).apply {
                logger.info("Registering item $name with $itemId")
            }

        private fun register(statusId: Identifier, statusEffect: StatusEffect) =
            Registry.register(Registries.STATUS_EFFECT, statusId, statusEffect)
                .apply { logger.info("Registering statusEffect $name with $statusId") }

        private fun register(blockId: Identifier, block: Block): Block =
            Registry.register(Registries.BLOCK, blockId, block).apply {
                logger.info("Registering block $name with $blockId") }
                .also { register(blockId, BlockItem(block, FabricItemSettings())) }


        private fun registerAnnotation(modId: String, field: KCallable<*>, instance: Any?) {
            val annotation = field.annotations.find { it is AutoRegister } as? AutoRegister ?: return
            val registrable = field.call(instance)
            enqueue(registrable, annotation.id, modId)
        }

        fun processAutoRegisterItems(classes: List<KClass<out Any>>) {

            classes.filter { clazz -> clazz.annotations.find { it is AutoRegisterClass } != null }
                .forEach { clazz ->
                    val modId = clazz.annotations.find { it is AutoRegisterClass } as? AutoRegisterClass
                    val companionInstance = clazz.companionObjectInstance
                    val instance = clazz.objectInstance
                    clazz.companionObject?.declaredMembers?.forEach {
                        registerAnnotation(
                            modId?.modId ?: "default", it, companionInstance
                        )
                    }
                    clazz.declaredMembers.forEach { registerAnnotation(modId?.modId ?: "default", it, instance) }
                }

            processQueues()
        }

        private fun processQueues() {
            itemsToRegister.forEach { (key, value) ->  register(key,value)}
            effectsToRegister.forEach { (key, value) ->  register(key,value)}
            blocksToRegister.forEach { (k,v) -> register(k,v)}
        }

        private fun <T> enqueue(obj: T, id: String, modId: String) {
            val identifier = id.toModId(modId)
            if (identifier == null) {
                logger.error("Failed to register $modId-$id, invalid id")
                return
            }
            when (obj) {
                is Item -> itemsToRegister.insertIfNotPresentElseLog(identifier, obj)
                is StatusEffect -> effectsToRegister.insertIfNotPresentElseLog(identifier, obj)
                is Block -> blocksToRegister.insertIfNotPresentElseLog(identifier, obj)
            }
        }

        private fun <K, V> MutableMap<K, V>.insertIfNotPresentElseLog(key: K, value: V) {
            if (containsKey(key)) {
                val oldValue = get(key)
                logger.warn("Overwriting $key trying to write $value on $oldValue, skipping")
                return
            } else {
                put(key, value)
            }
        }
    }
}

