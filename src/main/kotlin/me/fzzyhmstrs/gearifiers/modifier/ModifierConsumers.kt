package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.item.ItemStack

object ModifierConsumers {

    val DEMONIC_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            if (user.world.random.nextFloat() < 0.3333333f){
                EffectQueue.addStatusToQueue(target,StatusEffects.WEAKNESS,50,1)
            }
        }

    val MANIC_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            if (user.world.random.nextFloat() < 0.3333333f){
                EffectQueue.addStatusToQueue(target,StatusEffects.WEAKNESS,50,1)
            }
        }
        
    val SPLITTING_MINE_CONSUMER: EquipmentModifier.MineConsumer =
        EquipmentModifier.MineConsumer { stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: PlayerEntity ->
            if (state.isIn(BlockTags.LOGS)){
                if (world.random.nextFloat() < 0.1){
                    val sticks = world.random.nextInt(3) + 1
                    val stickEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(Items.STICK,sticks))
                    world.spawnEntity(stickEntity)
                }
            }
        }
        
    val ANTHRACITIC_MINE_CONSUMER: EquipmentModifier.MineConsumer =
        EquipmentModifier.MineConsumer { stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: PlayerEntity ->
            if (state.block is OreBlock){
                if (world.random.nextFloat() < 0.04){
                    val coals = world.random.nextInt(3) + 1
                    val coalEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(Items.COAL,coals))
                    world.spawnEntity(coalEntity)
                }
            }
        }
        
    private val METALS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("gearifiers","basic_metals"))
    private val METALS: List<Item> by lazy{
        
    }
    
    val METALLIC_MINE_CONSUMER: EquipmentModifier.MineConsumer =
        EquipmentModifier.MineConsumer { stack: ItemStack, world: World, state: BlockState, pos: BlockPos, miner: PlayerEntity ->
            if (state.block is OreBlock){
                if (world.random.nextFloat() < 0.03){
                    val metals = world.random.nextInt(3) + 1
                    val metalItem = METALS[world.random.nextInt(METALS.size)]
                    val metalEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(metalItem,metals))
                    world.spawnEntity(metalEntity)
                }
            }
        }
        
    private val GEMS_TAG: TagKey<Item> = TagKey.of(Registry.ITEM_KEY,Identifier("gearifiers","basic_gems"))
        
    class ThievingKillConsumer(private val chance: Float): EquipmentModifier.ToolConsumer{ 
        override fun apply(stack: ItemStack, user: LivingEntity, target: LivingEntity?){
            if (target != null && user.world.random.nextFloat() < chance){
                val damage = target.getRecentDamageSource()
                if (damage != null){
                    target.dropLoot(damage,true)
                }
            }
        }
    }
}
