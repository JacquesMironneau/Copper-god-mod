package com.pashmi.annotations

import com.pashmi.CopperGodMod.toModId
import com.pashmi.utils.logger
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
annotation class AutoRegisterItem(val id: String)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AutoRegisterItemClass(val modId: String)

class RegisterManager {

    companion object {
        private val logger = logger()

        private fun register(modId: String, id: String, item: Item): Item {
            val itemId = id.toModId()
            val res = Registry.register(Registries.ITEM, itemId, item)
            logger.info("Registering ${res.name} with $itemId")
            return res
        }

        private fun registerAnnotation(modId: String, field: KCallable<*>, instance: Any?) {
            val annotation = field.annotations.find { it is AutoRegisterItem } as? AutoRegisterItem
            if (annotation == null) return
            val item = field.call(instance) as Item
            register(modId, annotation.id, item)
        }

        fun processAutoRegisterItems(classes: List<KClass<out Any>>) {

            classes.filter { clazz -> clazz.annotations.find { it is AutoRegisterItemClass } != null }
                .forEach { clazz ->
                    val modId = clazz.annotations.find { it is AutoRegisterItemClass } as? AutoRegisterItemClass
                    val companionInstance = clazz.companionObjectInstance
                    val instance = clazz.objectInstance
                    clazz.companionObject?.declaredMembers?.forEach {
                        registerAnnotation(
                            modId?.modId ?: "default", it, companionInstance
                        )
                    }
                    clazz.declaredMembers.forEach { registerAnnotation(modId?.modId ?: "default", it, instance) }
                }
        }
    }
}

