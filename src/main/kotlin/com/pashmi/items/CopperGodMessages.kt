

import net.minecraft.text.Text
import net.minecraft.text.Style
import net.minecraft.text.TextColor
import kotlin.random.Random

class CopperGodMessages {

    companion object {

        val copper_style = Style.EMPTY.withColor(TextColor.fromRgb(16750848))
        val PREFIX = "[Copper god] "

        val messages = listOf (
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

        fun get_random_message(): Text {
            val msg = Text.literal(PREFIX + messages.get(Random.nextInt(messages.size)))
            msg.style = copper_style
            return msg
        }
    }
}