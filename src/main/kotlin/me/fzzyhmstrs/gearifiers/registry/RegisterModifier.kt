@file:Suppress("MemberVisibilityCanBePrivate")

package me.fzzyhmstrs.gearifiers.registry

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.modifier_util.AbstractModifier
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry
import me.fzzyhmstrs.gearifiers.Gearifiers
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import me.fzzyhmstrs.gearifiers.modifier.ConfigEquipmentModifier
import me.fzzyhmstrs.gearifiers.modifier.ModifierCommand
import me.fzzyhmstrs.gearifiers.modifier.ModifierConsumers
import me.fzzyhmstrs.gearifiers.modifier.ModifierFunctions
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.item.PickaxeItem
import net.minecraft.item.ShieldItem
import net.minecraft.loot.provider.number.BinomialLootNumberProvider
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object RegisterModifier {

    private val regMod: MutableList<AbstractModifier<*>> = mutableListOf()
    internal val defaultEnabledMap: MutableMap<String,Boolean> = mutableMapOf()
    
    private val CHEAP_TOLL = ConstantLootNumberProvider.create(3f) // 96% probability
    // private val NORMAL_TOLL = ConstantLootNumberProvider.create(5f) //75% probability
    private val EXPENSIVE_TOLL = UniformLootNumberProvider.create(6f,7f) //50% chance of enough toll
    private val VERY_EXPENSIVE_TOLL = UniformLootNumberProvider.create(7f,9f) //25% chance of toll
    private val RANDOM_TOLL = BinomialLootNumberProvider.create(20,0.5f)

    private val TRINKET_AND_SHIELD = object: EquipmentModifier.EquipmentModifierTarget(Identifier(Gearifiers.MOD_ID,"trinket_and_shield")){
        override fun isAcceptableItem(stack: ItemStack): Boolean {
            return TRINKET.isItemAcceptableOrTagged(stack) || stack.item is ShieldItem
        }
    }
    
    private val TRINKET_AND_ARMOR_AND_SHIELD = object: EquipmentModifier.EquipmentModifierTarget(Identifier(Gearifiers.MOD_ID,"trinket_and_armor_and_shield")){
        override fun isAcceptableItem(stack: ItemStack): Boolean {
            return TRINKET_AND_SHIELD.isItemAcceptableOrTagged(stack) || ARMOR.isItemAcceptableOrTagged(stack)
        }
    }

    private val PICKAXE = object: EquipmentModifier.EquipmentModifierTarget(Identifier(Gearifiers.MOD_ID,"pickaxe")){
        override fun isAcceptableItem(stack: ItemStack): Boolean {
            return stack.item is PickaxeItem
        }
    }

    private val ARMOR_BOTTOM = object: EquipmentModifier.EquipmentModifierTarget(Identifier(Gearifiers.MOD_ID,"armor_bottom")){
        override fun isAcceptableItem(stack: ItemStack): Boolean {
            return ARMOR_LEGS.isItemAcceptableOrTagged(stack) || ARMOR_FEET.isItemAcceptableOrTagged(stack)
        }
    }
    
    private fun buildModifier(modifierId: Identifier, target: EquipmentModifier.EquipmentModifierTarget, weight: Int = 10, rarity: EquipmentModifier.Rarity = EquipmentModifier.Rarity.COMMON): EquipmentModifier{
        val key = modifierId.toString()
        if (!GearifiersConfig.modifiers.enabledModifiers.containsKey(key)){
            defaultEnabledMap[key] = true
            GearifiersConfig.modifiers.enabledModifiers = defaultEnabledMap
        } else{
            defaultEnabledMap[key] = GearifiersConfig.modifiers.enabledModifiers[key]?:true
        }
        return ConfigEquipmentModifier(modifierId,target,weight,rarity)
    }
    
    //legendary modifiers
    val LEGENDARY = buildModifier(Identifier(Gearifiers.MOD_ID,"legendary"), EquipmentModifier.EquipmentModifierTarget.ANY, 1,EquipmentModifier.Rarity.LEGENDARY)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,1.5, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,0.05, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDurabilityMod(PerLvlI(0,0,25))
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    val VORPAL = buildModifier(Identifier(Gearifiers.MOD_ID,"vorpal"), EquipmentModifier.EquipmentModifierTarget.SWORD,1,EquipmentModifier.Rarity.LEGENDARY)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.20, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withPostHit(ModifierConsumers.VORPAL_HIT_CONSUMER)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    
    //basic damage modification for weapons
    val DEMONIC = buildModifier(Identifier(Gearifiers.MOD_ID,"demonic"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 3, EquipmentModifier.Rarity.LEGENDARY)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_KNOCKBACK,0.25, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,2.5, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,0.5, EntityAttributeModifier.Operation.ADDITION)
        .withPostHit(ModifierConsumers.DEMONIC_HIT_CONSUMER)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val UNGODLY_SHARP = buildModifier(Identifier(Gearifiers.MOD_ID,"ungodly_sharp"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 3,EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,2.0, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.05, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(DEMONIC)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val RAZOR_SHARP = buildModifier(Identifier(Gearifiers.MOD_ID,"razor_sharp"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 6,EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,1.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(UNGODLY_SHARP)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val KEEN = buildModifier(Identifier(Gearifiers.MOD_ID,"keen"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 7,EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(RAZOR_SHARP)
        .also { regMod.add(it) }
    val HONED = buildModifier(Identifier(Gearifiers.MOD_ID,"honed"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 12)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,0.75, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(KEEN)
        .also { regMod.add(it) }
    val SHARP = buildModifier(Identifier(Gearifiers.MOD_ID,"sharp"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 12)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HONED)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val DULL = buildModifier(Identifier(Gearifiers.MOD_ID,"dull"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 8,EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,-0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(SHARP)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val BLUNT = buildModifier(Identifier(Gearifiers.MOD_ID,"blunt"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 5,EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,-1.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(DULL)
        .also { regMod.add(it) }
    val USELESS = buildModifier(Identifier(Gearifiers.MOD_ID,"useless"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 3,EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,-2.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(BLUNT)
        .also { regMod.add(it) }

    //generic protection attributes
    val INDOMITABLE = buildModifier(Identifier(Gearifiers.MOD_ID,"indomitable"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 3, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,2.0, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,0.1, EntityAttributeModifier.Operation.ADDITION)
        .withOnDamaged(ModifierFunctions.INDOMITABLE_DAMAGE_FUNCTION)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val BULWARK = buildModifier(Identifier(Gearifiers.MOD_ID,"bulwark"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 5, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(INDOMITABLE)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val PROTECTIVE = buildModifier(Identifier(Gearifiers.MOD_ID,"protective"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 7, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,0.5, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,0.25, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(BULWARK)
        .also { regMod.add(it) }
    val THICK = buildModifier(Identifier(Gearifiers.MOD_ID,"thick"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,0.25, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(PROTECTIVE)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val THIN = buildModifier(Identifier(Gearifiers.MOD_ID,"thin"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET,7, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,-0.25, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(THICK)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val UNSUBSTANTIAL = buildModifier(Identifier(Gearifiers.MOD_ID,"unsubstantial"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 3, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,-0.75, EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,-0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(THIN)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //basic durability modifiers
    val EVERLASTING = buildModifier(Identifier(Gearifiers.MOD_ID,"everlasting"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 3, EquipmentModifier.Rarity.RARE)
        .withDurabilityMod(PerLvlI(50,0,50))
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val ROBUST = buildModifier(Identifier(Gearifiers.MOD_ID,"robust"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 7, EquipmentModifier.Rarity.UNCOMMON)
        .withDurabilityMod(PerLvlI(0,0,25))
        .withDescendant(EVERLASTING)
        .also { regMod.add(it) }
    val DURABLE = buildModifier(Identifier(Gearifiers.MOD_ID,"durable"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE)
        .withDurabilityMod(PerLvlI(0,0,10))
        .withDescendant(ROBUST)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val TATTERED = buildModifier(Identifier(Gearifiers.MOD_ID,"tattered"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE,8, EquipmentModifier.Rarity.BAD)
        .withDurabilityMod(PerLvlI(0,0,-10))
        .withDescendant(DURABLE)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val DISREPAIRED = buildModifier(Identifier(Gearifiers.MOD_ID,"disrepaired"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 6, EquipmentModifier.Rarity.BAD)
        .withDurabilityMod(PerLvlI(0,0,-25))
        .withDescendant(TATTERED)
        .also { regMod.add(it) }
    val DESTROYED = buildModifier(Identifier(Gearifiers.MOD_ID,"destroyed"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 2, EquipmentModifier.Rarity.REALLY_BAD)
        .withDurabilityMod(PerLvlI(-50,0,-50))
        .withDescendant(DISREPAIRED)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
        
    //basic attack speed modifiers
    val MANIC = buildModifier(Identifier(Gearifiers.MOD_ID,"manic"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 4, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.35, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withPostHit(ModifierConsumers.MANIC_HIT_CONSUMER)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val FRENZIED = buildModifier(Identifier(Gearifiers.MOD_ID,"frenzied"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 6, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.20, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(MANIC)
        .also { regMod.add(it) }
    val ENERGETIC = buildModifier(Identifier(Gearifiers.MOD_ID,"energetic"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,0.10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(FRENZIED)
        .also { regMod.add(it) }
    val CLUMSY = buildModifier(Identifier(Gearifiers.MOD_ID,"clumsy"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 8, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,-0.10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(ENERGETIC)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val UNWIELDY = buildModifier(Identifier(Gearifiers.MOD_ID,"unwieldy"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 4, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,-0.25, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(CLUMSY)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
        
    //basic health modifiers
    val FLOURISHING = buildModifier(Identifier(Gearifiers.MOD_ID,"flourishing"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 3, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,2.0, EntityAttributeModifier.Operation.ADDITION)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val HEARTY = buildModifier(Identifier(Gearifiers.MOD_ID,"hearty"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 5, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(FLOURISHING)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val HEALTHY = buildModifier(Identifier(Gearifiers.MOD_ID,"healthy"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 7, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HEARTY)
        .also { regMod.add(it) }
    val INFIRM = buildModifier(Identifier(Gearifiers.MOD_ID,"infirm"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 6, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,-0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HEALTHY)
        .also { regMod.add(it) }
    val WIMPY = buildModifier(Identifier(Gearifiers.MOD_ID,"wimpy"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 3, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,-1.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(INFIRM)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //Luck modifiers
    val LUCKY = buildModifier(Identifier(Gearifiers.MOD_ID,"lucky"), EquipmentModifier.EquipmentModifierTarget.ANY, 5, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_LUCK,1.0, EntityAttributeModifier.Operation.ADDITION)
        .also { regMod.add(it) }
    val UNLUCKY = buildModifier(Identifier(Gearifiers.MOD_ID,"unlucky"), EquipmentModifier.EquipmentModifierTarget.ANY, 3, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_LUCK,-1.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(LUCKY)
        .also { regMod.add(it) }
    
    //basic movement speed modifiers
    val RACING = buildModifier(Identifier(Gearifiers.MOD_ID,"racing"), ARMOR_BOTTOM, 3, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,0.10, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val SPEEDY = buildModifier(Identifier(Gearifiers.MOD_ID,"speedy"), ARMOR_BOTTOM, 7, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,0.05, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(RACING)
        .also { regMod.add(it) }
    val QUICK = buildModifier(Identifier(Gearifiers.MOD_ID,"quick"), ARMOR_BOTTOM, 10, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,0.025, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(SPEEDY)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val SLOW = buildModifier(Identifier(Gearifiers.MOD_ID,"slow"), ARMOR_BOTTOM, 7, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,-0.025, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(QUICK)
        .also { regMod.add(it) }
    val SLUGGISH = buildModifier(Identifier(Gearifiers.MOD_ID,"sluggish"), ARMOR_BOTTOM, 5, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,-0.05, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(SLOW)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //attack range modifiers
    val GREATER_EXTENSION = buildModifier(Identifier(Gearifiers.MOD_ID,"greater_extension"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 2, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,1.5, EntityAttributeModifier.Operation.ADDITION)
        .also { regMod.add(it) }
    val EXTENSION = buildModifier(Identifier(Gearifiers.MOD_ID,"extension"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 5, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,0.75, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(GREATER_EXTENSION)
        .also { regMod.add(it) }
    val LESSER_EXTENSION = buildModifier(Identifier(Gearifiers.MOD_ID,"lesser_extension"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 8)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,0.25, EntityAttributeModifier.Operation.ADDITION)
        .withToll(CHEAP_TOLL)
        .withDescendant(EXTENSION)
        .also { regMod.add(it) }
    val LESSER_STUBBY = buildModifier(Identifier(Gearifiers.MOD_ID,"lesser_stubby"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 7, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,-0.25, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(LESSER_EXTENSION)
        .also { regMod.add(it) }
    val STUBBY = buildModifier(Identifier(Gearifiers.MOD_ID,"stubby"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 4, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,-0.75, EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(LESSER_STUBBY)
        .also { regMod.add(it) }

    //reach distance modifiers
    val GRAND_REACH = buildModifier(Identifier(Gearifiers.MOD_ID,"grand_reach"), EquipmentModifier.EquipmentModifierTarget.MINING, 4, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,2.5, EntityAttributeModifier.Operation.ADDITION)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val GREATER_REACH = buildModifier(Identifier(Gearifiers.MOD_ID,"greater_reach"), EquipmentModifier.EquipmentModifierTarget.MINING, 4, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,1.5, EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(GRAND_REACH)
        .also { regMod.add(it) }
    val REACH = buildModifier(Identifier(Gearifiers.MOD_ID,"reach"), EquipmentModifier.EquipmentModifierTarget.MINING, 6, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,1.0, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(GREATER_REACH)
        .also { regMod.add(it) }
    val LESSER_REACH = buildModifier(Identifier(Gearifiers.MOD_ID,"lesser_reach"), EquipmentModifier.EquipmentModifierTarget.MINING,11)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(REACH)
        .also { regMod.add(it) }
    val LESSER_LIMITING = buildModifier(Identifier(Gearifiers.MOD_ID,"lesser_limiting"), EquipmentModifier.EquipmentModifierTarget.MINING, 5, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,-0.5, EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(LESSER_REACH)
        .also { regMod.add(it) }
    val LIMITING = buildModifier(Identifier(Gearifiers.MOD_ID,"limiting"), EquipmentModifier.EquipmentModifierTarget.MINING, 2, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,-1.25, EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(LESSER_LIMITING)
        .also { regMod.add(it) }

    //directly damage modification modifiers
    val BLUNTING = buildModifier(Identifier(Gearifiers.MOD_ID,"blunting"), TRINKET_AND_ARMOR_AND_SHIELD,3,EquipmentModifier.Rarity.EPIC)
        .withOnDamaged(ModifierFunctions.DamageMultiplierFunction(-.05f))
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val DULLING = buildModifier(Identifier(Gearifiers.MOD_ID,"dulling"), TRINKET_AND_ARMOR_AND_SHIELD,6,EquipmentModifier.Rarity.UNCOMMON)
        .withOnDamaged(ModifierFunctions.DamageMultiplierFunction(-.025f))
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(BLUNTING)
        .also { regMod.add(it) }
    val HARSH = buildModifier(Identifier(Gearifiers.MOD_ID,"harsh"), TRINKET_AND_ARMOR_AND_SHIELD,5,EquipmentModifier.Rarity.BAD)
        .withOnDamaged(ModifierFunctions.DamageMultiplierFunction(.025f))
        .withDescendant(DULLING)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val REDUCTIVE = buildModifier(Identifier(Gearifiers.MOD_ID,"reductive"), TRINKET_AND_ARMOR_AND_SHIELD,4,EquipmentModifier.Rarity.RARE)
        .withOnDamaged(ModifierFunctions.DamageAdderFunction(-.25f))
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val ADDITIVE = buildModifier(Identifier(Gearifiers.MOD_ID,"additive"), TRINKET_AND_ARMOR_AND_SHIELD,2,EquipmentModifier.Rarity.REALLY_BAD)
        .withOnDamaged(ModifierFunctions.DamageAdderFunction(.25f))
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(REDUCTIVE)
        .also { regMod.add(it) }

    //misc modifiers
    val SHIELDING = buildModifier(Identifier(Gearifiers.MOD_ID,"shielding"), TRINKET_AND_SHIELD,5,EquipmentModifier.Rarity.UNCOMMON)
        .withOnDamaged(ModifierFunctions.SHIELDING_DAMAGE_FUNCTION)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val POISONOUS = buildModifier(Identifier(Gearifiers.MOD_ID,"poisonous"), TRINKET_AND_SHIELD,2,EquipmentModifier.Rarity.RARE)
        .withOnDamaged(ModifierFunctions.PoisonousDamageFunction(120, 1))
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val TOXIC = buildModifier(Identifier(Gearifiers.MOD_ID,"toxic"), TRINKET_AND_SHIELD,5,EquipmentModifier.Rarity.UNCOMMON)
        .withOnDamaged(ModifierFunctions.PoisonousDamageFunction(100, 0))
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(POISONOUS)
        .also { regMod.add(it) }
    val DESECRATED = buildModifier(Identifier(Gearifiers.MOD_ID,"desecrated"), TRINKET_AND_SHIELD,1,EquipmentModifier.Rarity.EPIC)
        .withOnDamaged(ModifierFunctions.DesecratedDamageFunction(120, 1))
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val UNHOLY = buildModifier(Identifier(Gearifiers.MOD_ID,"unholy"), TRINKET_AND_SHIELD,3,EquipmentModifier.Rarity.RARE)
        .withOnDamaged(ModifierFunctions.DesecratedDamageFunction(100, 0))
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(DESECRATED)
        .also { regMod.add(it) }
    val FORCEFUL = buildModifier(Identifier(Gearifiers.MOD_ID,"forceful"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET,3,EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .also { regMod.add(it) }
    val GREATER_THIEVING = buildModifier(Identifier(Gearifiers.MOD_ID,"greater_thieving"), EquipmentModifier.EquipmentModifierTarget.WEAPON,2,EquipmentModifier.Rarity.EPIC)
        .withKilledOther(ModifierConsumers.ThievingKillConsumer(0.15f))
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val THIEVING = buildModifier(Identifier(Gearifiers.MOD_ID,"thieving"), EquipmentModifier.EquipmentModifierTarget.WEAPON,5,EquipmentModifier.Rarity.RARE)
        .withKilledOther(ModifierConsumers.ThievingKillConsumer(0.1f))
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(GREATER_THIEVING)
        .also { regMod.add(it) }
    val LESSER_THIEVING = buildModifier(Identifier(Gearifiers.MOD_ID,"lesser_thieving"), EquipmentModifier.EquipmentModifierTarget.WEAPON,7,EquipmentModifier.Rarity.UNCOMMON)
        .withKilledOther(ModifierConsumers.ThievingKillConsumer(0.05f))
        .withDescendant(THIEVING)
        .also { regMod.add(it) }
    val SPLITTING = buildModifier(Identifier(Gearifiers.MOD_ID,"splitting"), EquipmentModifier.EquipmentModifierTarget.AXE,5,EquipmentModifier.Rarity.UNCOMMON)
        .withPostMine(ModifierConsumers.SPLITTING_MINE_CONSUMER)
        .also { regMod.add(it) }
    val ENRICHED = buildModifier(Identifier(Gearifiers.MOD_ID,"enriched"), PICKAXE,2,EquipmentModifier.Rarity.EPIC)
        .withPostMine(ModifierConsumers.ENRICHED_MINE_CONSUMER)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val METALLIC = buildModifier(Identifier(Gearifiers.MOD_ID,"metallic"), PICKAXE,4,EquipmentModifier.Rarity.RARE)
        .withPostMine(ModifierConsumers.METALLIC_MINE_CONSUMER)
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(ENRICHED)
        .also { regMod.add(it) }
    val ANTHRACITIC = buildModifier(Identifier(Gearifiers.MOD_ID,"anthracitic"), PICKAXE,7,EquipmentModifier.Rarity.UNCOMMON)
        .withPostMine(ModifierConsumers.ANTHRACITIC_MINE_CONSUMER)
        .withDescendant(METALLIC)
        .also { regMod.add(it) }

    //misc bad modifiers
    val DOUBLE_EDGED = buildModifier(Identifier(Gearifiers.MOD_ID,"double_edged"), EquipmentModifier.EquipmentModifierTarget.TOOL,3,EquipmentModifier.Rarity.BAD)
        .withPostHit(ModifierConsumers.DOUBLE_EDGED_HIT_CONSUMER)
        .also { regMod.add(it) }
    val JARRING = buildModifier(Identifier(Gearifiers.MOD_ID,"jarring"), EquipmentModifier.EquipmentModifierTarget.TOOL,3,EquipmentModifier.Rarity.BAD)
        .withPostHit(ModifierConsumers.JARRING_HIT_CONSUMER)
        .also { regMod.add(it) }
    val CLANGING = buildModifier(Identifier(Gearifiers.MOD_ID,"clanging"), EquipmentModifier.EquipmentModifierTarget.TOOL,3,EquipmentModifier.Rarity.BAD)
        .withPostHit(ModifierConsumers.CLANGING_HIT_CONSUMER)
        .also { regMod.add(it) }
    val GREATER_CRUMBLING = buildModifier(Identifier(Gearifiers.MOD_ID,"greater_crumbling"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE,3,EquipmentModifier.Rarity.REALLY_BAD)
        .withOnDamaged(ModifierFunctions.CrumblingDamageFunction(0.5f))
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val CRUMBLING = buildModifier(Identifier(Gearifiers.MOD_ID,"crumbling"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE,4,EquipmentModifier.Rarity.BAD)
        .withOnDamaged(ModifierFunctions.CrumblingDamageFunction(0.25f))
        .withDescendant(GREATER_CRUMBLING)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    fun registerAll(){
        regMod.forEach {
            val id = it.modifierId
            defaultEnabledMap[id.toString()] = true
            ModifierRegistry.register(it)
            ModifierCommand.modifierList.add(id)
        }
    }

}
