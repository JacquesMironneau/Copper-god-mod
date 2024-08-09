package com.pashmi.items


import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import kotlin.random.Random

class CopperGodMessages {

    companion object {

        val copper_style: Style = Style.EMPTY.withColor(TextColor.fromRgb(16750848)).withBold(true)
        val copper_style_italics: Style = copper_style.withItalic(true).withBold(false)
        private const val PREFIX = "[Copper god] "

        private val messages = listOf(
            "Become CONDUCTIVE, I command you !",
            "Oxidize and perish !",
            "Yield to my COPPER WILL",
            "You are but worthless dross!",
            "You are nothing but slag in my forge!",
            "How insignificant you are in copper's glow!",
            "Feel the wrath of the copper god!",
            "Become malleable in my hands of death!",
            "Corrosion consumes you!",
            "Like copper exposed to acid, you dissolve!",
            "Your life force will be drained like copper from a mine!"
        )

        fun getRandomAngryMessage(): Text =
            Text.literal(PREFIX + messages[Random.nextInt(messages.size)]).also { it.style = copper_style }

        fun getRandomAllyMessage(): Text =
            Text.literal(PREFIX + allyMessages[Random.nextInt(allyMessages.size)]).also { it.style = copper_style }

        fun getRefillMessage(amount: Int): Text =
            Text.literal("$PREFIX Your prayers have been heard ! You earned $amount charges")
                .also { it.style = copper_style }

        fun getToolRefillMessage(amount: Int): Text = Text.literal("$PREFIX Your prayers have been heard ! Mine and discover my POWER")

        private val allyMessages = listOf(
            "Cuuuuuuuuuuuuuuuuuuu",
            "They shall BURN",
            "Indeed my servant",
            "They must pass on!",
            "After that, investigations will be hard to CONDUCT",
            "Get'them back to FORTNITE"

        )


    }
}