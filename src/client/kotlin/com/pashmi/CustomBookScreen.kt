package com.pashmi

import com.pashmi.CopperGodMod.toModId
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.BookScreen
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text

class CustomBookScreen(private val itemStack: ItemStack) : BookScreen() {
    init {
        setDefaultText(itemStack)
    }

    override fun init() {
        super.init()
        this.setPageProvider(WrittenBookContents(itemStack))
    }

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.renderBackground(context, mouseX, mouseY, delta)
        context.drawTexture(COPPER_TABLET_TEXTURE, (this.width - 192) / 2, 2, 0, 0, 192, 192)

    }

    //    @Override
    //    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    //        super.render(context, mouseX, mouseY, delta);
    //    }
    private fun setDefaultText(book: ItemStack) {

        val nbt = book.getOrCreateNbt()
        nbt.putString("author", "Copper servant")
        nbt.putString("title", "Copper god how-to")

        // Create a list for the pages.
        val pages = NbtList()
        pages.add(NbtString.of(DEFAULT_TEXT_1))
        pages.add(NbtString.of(DEFAULT_TEXT_2))
        pages.add(NbtString.of(DEFAULT_TEXT_3))

        nbt.put("pages", pages)
        book.nbt = nbt
    }

    companion object {
        private var DEFAULT_TEXT_1 = """${Text.translatable("pashmi-copper-god.book-second_1").string}
               
${Text.translatable("pashmi-copper-god.book-first_2").string}

${Text.translatable("pashmi-copper-god.book-first_3").string}

${Text.translatable("pashmi-copper-god.book-first_4").string}
                
${Text.translatable("pashmi-copper-god.book-first_5").string}"""

        private var DEFAULT_TEXT_2 = """${Text.translatable("pashmi-copper-god.book-second_1").string}
${Text.translatable("pashmi-copper-god.book-second_2").string}
${Text.translatable("pashmi-copper-god.book-second_3").string}
   
${Text.translatable("pashmi-copper-god.book-second_4").string}
${Text.translatable("pashmi-copper-god.book-second_5").string}
${Text.translatable("pashmi-copper-god.book-second_6").string}
${Text.translatable("pashmi-copper-god.book-second_7").string}
${Text.translatable("pashmi-copper-god.book-second_8").string}"""

        private var DEFAULT_TEXT_3 = Text.translatable("pashmi-copper-god.book-third_1").string

        private val COPPER_TABLET_TEXTURE = "textures/gui/copper_tablet.png".toModId()
    }
}