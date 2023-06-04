package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.fzzy_core.trinket_util.EffectQueue
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gearifiers.mixins.LivingEntityAccessor
import net.minecraft.block.BlockState
import net.minecraft.block.ExperienceDroppingBlock
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.BlockTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.math.max
import kotlin.math.min

object ModifierConsumers {

    val VORPAL_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            val damageSource = target.recentDamageSource ?: return@ToolConsumer
            val recentDamage = target.damageTracker.mostRecentDamage?.damage ?: return@ToolConsumer
            if (user.world.random.nextFloat() < GearifiersConfig.chances.vorpalChance){
                target.isInvulnerable = false
                target.damage(damageSource,recentDamage * 9f)
            }
        }

    val DEMONIC_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            if (user.world.random.nextFloat() < GearifiersConfig.chances.demonicChance){
                target.addStatusEffect(StatusEffectInstance( StatusEffects.WEAKNESS,50,1))
            }
        }

    val MANIC_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, target: LivingEntity? ->
            if (target == null) return@ToolConsumer
            if (user.world.random.nextFloat() < GearifiersConfig.chances.manicChance){
                if (user.hasStatusEffect(StatusEffects.HASTE)){
                    val effect = user.getStatusEffect(StatusEffects.HASTE)
                    val amp = effect?.amplifier?:0
                    val duration = effect?.duration?:0
                    if (duration > 0){
                        val duration2 = if(duration < 60) {60} else {duration}
                        user.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE,duration2,min(5,amp + 1)))
                    }
                } else {
                    user.addStatusEffect(StatusEffectInstance(StatusEffects.HASTE, 60, 0))
                }
            }
        }

    val JARRING_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, _: LivingEntity? ->
            if (user.world.random.nextFloat() < GearifiersConfig.chances.jarringChance){
                user.addStatusEffect(StatusEffectInstance(StatusEffects.SLOWNESS,50,1))
            }
        }

    val CLANGING_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, _: LivingEntity? ->
            if (user.world.random.nextFloat() < GearifiersConfig.chances.clangingChance){
                user.addStatusEffect(StatusEffectInstance(StatusEffects.WEAKNESS,50,1))
            }
        }

    val DOUBLE_EDGED_HIT_CONSUMER: EquipmentModifier.ToolConsumer =
        EquipmentModifier.ToolConsumer { _: ItemStack, user: LivingEntity, _: LivingEntity? ->
            if (user.world.random.nextFloat() < GearifiersConfig.chances.doubleEdgedChance){
                user.damage(DamageSource.GENERIC,1.0f)
            }
        }
        
    val SPLITTING_MINE_CONSUMER: EquipmentModifier.MiningConsumer =
        EquipmentModifier.MiningConsumer { _: ItemStack, world: World, state: BlockState, pos: BlockPos, _: PlayerEntity ->
            if (state.isIn(BlockTags.LOGS)){
                if (world.random.nextFloat() < GearifiersConfig.chances.splittingChance){
                    val sticks = world.random.nextInt(3) + 1
                    val stickEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(Items.STICK,sticks))
                    world.spawnEntity(stickEntity)
                }
            }
        }
        
    val ANTHRACITIC_MINE_CONSUMER: EquipmentModifier.MiningConsumer =
        EquipmentModifier.MiningConsumer { _: ItemStack, world: World, state: BlockState, pos: BlockPos, _: PlayerEntity ->
            if (state.block is ExperienceDroppingBlock){
                if (world.random.nextFloat() < GearifiersConfig.chances.anthraciticChance){
                    val coals = world.random.nextInt(3) + 1
                    val coalEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(Items.COAL,coals))
                    world.spawnEntity(coalEntity)
                }
            }
        }
        
    private val METALS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM, Identifier("gearifiers","basic_metals"))
    private val METALS: List<Item> by lazy{
        val opt =  Registries.ITEM.getEntryList(
            METALS_TAG)
        if (opt.isPresent){
            opt.get().stream().map { entry -> entry.value() }.toList()
        } else {
            listOf()
        }
    }
    
    val METALLIC_MINE_CONSUMER: EquipmentModifier.MiningConsumer =
        EquipmentModifier.MiningConsumer { _: ItemStack, world: World, state: BlockState, pos: BlockPos, _: PlayerEntity ->
            if (state.block is ExperienceDroppingBlock){
                if (world.random.nextFloat() < GearifiersConfig.chances.metallicChance && METALS.isNotEmpty()){
                    val metals = world.random.nextInt(3) + 1
                    val metalItem = METALS[world.random.nextInt(METALS.size)]
                    val metalEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(metalItem,metals))
                    world.spawnEntity(metalEntity)
                }
            }
        }
        
    private val GEMS_TAG: TagKey<Item> = TagKey.of(RegistryKeys.ITEM,Identifier("gearifiers","basic_gems"))
    private val GEMS: List<Item> by lazy{
        val opt =  Registries.ITEM.getEntryList(
            GEMS_TAG)
        if (opt.isPresent){
            opt.get().stream().map { entry -> entry.value() }.toList()
        } else {
            listOf()
        }
    }

    val ENRICHED_MINE_CONSUMER: EquipmentModifier.MiningConsumer =
        EquipmentModifier.MiningConsumer { _: ItemStack, world: World, state: BlockState, pos: BlockPos, _: PlayerEntity ->
            if (state.block is ExperienceDroppingBlock){
                if (world.random.nextFloat() < GearifiersConfig.chances.enrichedChance && GEMS.isNotEmpty()){
                    val gems = world.random.nextInt(3) + 1
                    val gemItem = GEMS[world.random.nextInt(GEMS.size)]
                    val gemEntity = ItemEntity(world,pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(gemItem,gems))
                    world.spawnEntity(gemEntity)
                }
            }
        }
        
    class ThievingKillConsumer(private val chance: Float): EquipmentModifier.ToolConsumer{ 
        override fun apply(stack: ItemStack, user: LivingEntity, target: LivingEntity?){
            if (target != null && user.world.random.nextFloat() < chance){
                val damage = target.recentDamageSource
                if (damage != null){
                    (target as LivingEntityAccessor).gearifiers_callDropLoot(damage,true)
                }
            }
        }
    }
}
