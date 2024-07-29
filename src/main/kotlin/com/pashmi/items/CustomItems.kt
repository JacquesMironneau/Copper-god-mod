

import net.minecraft.item.ToolMaterials
import net.minecraft.item.ToolMaterial
import net.minecraft.item.Items
import net.minecraft.item.Item
import net.minecraft.item.SwordItem
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.Registry
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class CustomItems: ToolMaterial {

    companion object {
        val MOD_ID = "second-mod"

        val COPPERITE = CustomItems()

        fun initialize() {}

        fun register(item: Item, id: String): Item {
            val itemID = Identifier.of(MOD_ID, id);
            return Registry.register(Registries.ITEM, itemID, item)
        }

        val COPPER_SWORD = register(ThunderAppealedSword(COPPERITE, 7, 1.5F, Item.Settings()), "copper_sword")
        val THE_COPPER_SWORD = register(ThunderAppealedSword(COPPERITE, 10, 1.7F, Item.Settings()), "op_copper_sword")
    }

    override fun getDurability() = 200

    override fun getMiningSpeedMultiplier() = 1.2f

    override fun getAttackDamage()= 1.0f

    override fun getMiningLevel() =  20

    override fun getEnchantability() = 1

    override fun getRepairIngredient() = Ingredient.ofItems(Items.COPPER_BLOCK)
}