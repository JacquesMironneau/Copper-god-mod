

import net.minecraft.item.SwordItem
import net.minecraft.item.Item
import net.minecraft.item.ToolMaterial
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.LightningEntity
import net.minecraft.entity.EntityType
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.text.Text
import net.minecraft.text.Style
import net.minecraft.text.TextColor
import kotlin.random.Random 

class ThunderAppealedSword: SwordItem {

    constructor(toolMaterial: ToolMaterial, attackDamage: Int, attackSpeed: Float, settings: Item.Settings) : super(toolMaterial, attackDamage, attackSpeed, settings)
    
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {

        val itemStack: ItemStack = user.getStackInHand(hand)

        val copper_style = Style.EMPTY.withColor(TextColor.fromRgb(16750848))

        val willStrike = Random.nextInt(50) == 1
        
        if (willStrike) {
            user.sendMessage(CopperGodMessages.get_random_message())
            val thunderEntity = LightningEntity(EntityType.LIGHTNING_BOLT, world)
            thunderEntity.setPosition(user.getPos())
            world.spawnEntity(thunderEntity)
            return TypedActionResult.success(itemStack, true)
        } else {
            return TypedActionResult.pass(itemStack)
        }
    }
}