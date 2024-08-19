package com.pashmi.items


import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor

val copper_style: Style = Style.EMPTY.withColor(TextColor.fromRgb(16750848)).withBold(true)
val copper_style_italics: Style = copper_style.withItalic(true).withBold(false)

private val PREFIX: String?
    get() = Text.translatable("pashmi-copper-god.copper-god-message.prefix").string

private data class CopperGodText(val translatableKey: String, val arg: Int = -1)

private fun CopperGodText.toFormattedText(textStyle: Style = copper_style): Text {
    val translated = Text.translatable(translatableKey, arg).string
    return Text.literal("§6§l$PREFIX§r${translated}")
}

class CopperGodMessages {


    companion object {

        private val messages = listOf(
            CopperGodText("pashmi-copper-god.copper-god-message.worthless_dross"),
            CopperGodText("pashmi-copper-god.copper-god-message.slag_in_forge"),
            CopperGodText("pashmi-copper-god.copper-god-message.malleable_in_hands"),
            CopperGodText("pashmi-copper-god.copper-god-message.corrosion_consumes"),
            CopperGodText("pashmi-copper-god.copper-god-message.life_force_drained")
        )

        private val allyMessages = listOf(
            CopperGodText("pashmi-copper-god.copper-god-message.malleable_in_hands"),
            CopperGodText("pashmi-copper-god.copper-god-message.insignificant_in_glow"),
            CopperGodText("pashmi-copper-god.copper-god-message.oxidize_and_perish"),
            CopperGodText("pashmi-copper-god.copper-god-message.yield_to_copper_will"),
            CopperGodText("pashmi-copper-god.copper-god-message.become_conductive"),
            CopperGodText("pashmi-copper-god.copper-god-message.wrath_of_copper_god"),
            CopperGodText("pashmi-copper-god.copper-god-message.like_copper_exposed"),
            CopperGodText("pashmi-copper-god.copper-god-message.cuuuuuuuuuuuuuuuuuuu"),
            CopperGodText("pashmi-copper-god.copper-god-message.they_shall_burn"),
            CopperGodText("pashmi-copper-god.copper-god-message.indeed_my_servant"),
            CopperGodText("pashmi-copper-god.copper-god-message.they_must_pass_on"),
            CopperGodText("pashmi-copper-god.copper-god-message.investigations_hard_to_conduct"),
            CopperGodText("pashmi-copper-god.copper-god-message.get_them_back_to_fortnite")
        )


        fun getRandomAllyMessage(): Text =
            allyMessages.random().toFormattedText()

        fun getRefillMessage(amount: Int): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.prayers_heard_earned_charges", amount)
                .toFormattedText()

        fun getToolRefillMessage(amount: Int): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.prayers_heard_mine_discover", amount)
                .toFormattedText()

        fun getMoreCopper(amount: Int): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.only_amount_copper_need_more", amount)
                .toFormattedText()

        fun needCopper(): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.ridiculous_offer")
                .toFormattedText()

        fun getOratoryCreationMessage(): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.hello_mortals")
                .toFormattedText()

        fun getOratoryDestructionMessage(): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.my_presence_here_is_over")
                .toFormattedText()

        fun getCopperSpentMessage(): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.enjoy_hint_of_power")
                .toFormattedText()

        fun notWorthy(): Text =
            CopperGodText("pashmi-copper-god.copper-god-message.fool_think_worthy")
                .toFormattedText()
    }
}