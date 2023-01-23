package me.fzzyhmstrs.gearifiers.modifier

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes
import me.fzzyhmstrs.fzzy_core.coding_util.PerLvlI
import me.fzzyhmstrs.fzzy_core.modifier_util.AbstractModifier
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.fzzy_core.registry.ModifierRegistry
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.item.ShieldItem
import net.minecraft.loot.provider.number.BinomialLootNumberProvider
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.loot.provider.number.UniformLootNumberProvider
import net.minecraft.util.Identifier

object RegisterModifier {

    private val regMod: MutableList<AbstractModifier<*>> = mutableListOf()
    
    private val CHEAP_TOLL = ConstantLootNumberProvider.create(3f)
    private val EXPENSIVE_TOLL = ConstantLootNumberProvider.create(7f)
    private val VERY_EXPENSIVE_TOLL = ConstantLootNumberProvider.create(9f)
    private val RANDOM_TOLL = BinomialLootNumberProvider.create(10,0.5f)

    private val TRINKET_AND_SHIELD = object: EquipmentModifier.EquipmentModifierTarget(Identifier(Gearifiers.MOD_ID,"trinket_and_shield")){
        override fun isAcceptableItem(stack: ItemStack): Boolean {
            return TRINKET.isStackAcceptable(stack) || stack.item is ShieldItem
        }
    }
    
    //legendary modifier
    val LEGENDARY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"demonic"), EquipmentModifier.EquipmentModifierTarget.ANY, 1,EquipmentModifier.Rarity.LEGENDARY)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",-0.10,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",1.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,"450uy3dm-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,"3lf8a0d5-908f-11ed-a1eb-0242ac120002",0.05,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDurabilityMod(PerLvlI(0,0,25))
        .withToll(UniformLootNumberProvider.create(12f,18f))
        .also { regMod.add(it) }
    
    //basic damage modification for weapons
    val DEMONIC = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"demonic"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 2, EquipmentModifier.Rarity.LEGENDARY)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",0.15,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_KNOCKBACK,"6th9uh85-908f-11ed-a1eb-0242ac120002",0.25,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",2.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,"312hl65gm-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withPostHit(ModifierConsumers.DEMONIC_HIT_CONSUMER)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val UNGODLY_SHARP = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"ungodly_sharp"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 3,EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",0.05,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(DEMONIC)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val RAZOR_SHARP = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"razor_sharp"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 5,EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",1.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(UNGODLY_SHARP)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val KEEN = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"keen"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 7,EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(RAZOR_SHARP)
        .also { regMod.add(it) }
    val HONED = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"honed"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 10)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",0.75,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(KEEN)
        .also { regMod.add(it) }
    val SHARP = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"sharp"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 12)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HONED)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val DULL = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"dull"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 10,EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",-0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(SHARP)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val BLUNT = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"blunt"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 5,EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",-1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(DULL)
        .also { regMod.add(it) }
    val USELESS = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"useless"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 3,EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",-2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(BLUNT)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //generic protection attributes
    val INDOMITABLE = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"indomitable"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 2, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"568g491t-908f-11ed-a1eb-0242ac120002",2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,"2blq6mg1-908f-11ed-a1eb-0242ac120002",0.1,
            EntityAttributeModifier.Operation.ADDITION)
        .withOnDamaged(ModifierFunctions.INDOMITABLE_DAMAGE_FUNCTION)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val BULWARK = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"bulwark"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 3, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"568g491t-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(INDOMITABLE)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val PROTECTIVE = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"indomitable"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 6, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",0.75,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"568g491t-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(BULWARK)
        .also { regMod.add(it) }
    val THICK = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"thick"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(PROTECTIVE)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val THIN = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"thin"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET,10, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",-0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(THICK)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val UNSUBSTANTIAL = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"unsubstantial"), EquipmentModifier.EquipmentModifierTarget.ARMOR, 3, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR,"6fds7tpm-908f-11ed-a1eb-0242ac120002",-1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ARMOR_TOUGHNESS,"568g491t-908f-11ed-a1eb-0242ac120002",-0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(THIN)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //basic durability modifiers
    val EVERLASTING = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"everlasting"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 3, EquipmentModifier.Rarity.RARE)
        .withDurabilityMod(PerLvlI(50,0,50))
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val ROBUST = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"robust"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 7, EquipmentModifier.Rarity.UNCOMMON)
        .withDurabilityMod(PerLvlI(0,0,25))
        .withDescendant(EVERLASTING)
        .also { regMod.add(it) }
    val DURABLE = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"durable"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE)
        .withDurabilityMod(PerLvlI(0,0,10))
        .withDescendant(ROBUST)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val TATTERED = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"tattered"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE,10, EquipmentModifier.Rarity.BAD)
        .withDurabilityMod(PerLvlI(0,0,-10))
        .withDescendant(DURABLE)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val DISREPAIRED = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"disrepaired"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 6, EquipmentModifier.Rarity.BAD)
        .withDurabilityMod(PerLvlI(0,0,-25))
        .withDescendant(TATTERED)
        .also { regMod.add(it) }
    val DESTROYED = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"destroyed"), EquipmentModifier.EquipmentModifierTarget.BREAKABLE, 2, EquipmentModifier.Rarity.REALLY_BAD)
        .withDurabilityMod(PerLvlI(-50,0,-50))
        .withDescendant(DISREPAIRED)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
        
    //basic attack speed modifiers
    val MANIC = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"manic"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 3, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",0.35,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withPostHit(ModifierConsumers.MANIC_HIT_CONSUMER)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val FRENZIED = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"frenzied"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 6, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",0.20,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(MANIC)
        .also { regMod.add(it) }
    val ENERGETIC = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"energetic"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",0.10,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(FRENZIED)
        .also { regMod.add(it) }
    val CLUMSY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"clumsy"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 8, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",-0.10,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(ENERGETIC)
        .also { regMod.add(it) }
    val UNWIELDY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"unwieldy"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 5, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_SPEED,"5t8g9a6p-908f-11ed-a1eb-0242ac120002",-0.25,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(CLUMSY)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
        
    //basic health modifiers
    val FLOURISHING = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"flourishing"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 2, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,"450uy3dm-908f-11ed-a1eb-0242ac120002",2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val HEARTY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"hearty"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 5, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,"450uy3dm-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(FLOURISHING)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val HEALTHY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"healthy"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 7, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,"450uy3dm-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HEARTY)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val INFIRM = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"infirm"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 6, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,"450uy3dm-908f-11ed-a1eb-0242ac120002",-0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HEALTHY)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val WIMPY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"wimpy"), EquipmentModifier.EquipmentModifierTarget.ARMOR_AND_TRINKET, 4, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MAX_HEALTH,"450uy3dm-908f-11ed-a1eb-0242ac120002",-1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(INFIRM)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //Luck modifiers
    val LUCKY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"lucky"), EquipmentModifier.EquipmentModifierTarget.ANY, 5, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_LUCK,"qp41dsm7-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val UNLUCKY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"unlucky"), EquipmentModifier.EquipmentModifierTarget.ANY, 3, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_LUCK,"qp41dsm7-908f-11ed-a1eb-0242ac120002",-1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(LUCKY)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    
    //basic movement speed modifiers
    val RACING = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"racing"), EquipmentModifier.EquipmentModifierTarget.ARMOR_LEGS, 3, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,"3lf8a0d5-908f-11ed-a1eb-0242ac120002",0.10,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val SPEEDY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"speedy"), EquipmentModifier.EquipmentModifierTarget.ARMOR_LEGS, 6, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,"3lf8a0d5-908f-11ed-a1eb-0242ac120002",0.05,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(RACING)
        .also { regMod.add(it) }
    val QUICK = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"quick"), EquipmentModifier.EquipmentModifierTarget.ARMOR_LEGS)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,"3lf8a0d5-908f-11ed-a1eb-0242ac120002",0.025,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(SPEEDY)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val SLOW = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"slow"), EquipmentModifier.EquipmentModifierTarget.ARMOR_LEGS, 8, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,"3lf8a0d5-908f-11ed-a1eb-0242ac120002",-0.025,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(QUICK)
        .withToll(CHEAP_TOLL)
        .also { regMod.add(it) }
    val SLUGGISH = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"sluggish"), EquipmentModifier.EquipmentModifierTarget.ARMOR_LEGS, 4, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,"3lf8a0d5-908f-11ed-a1eb-0242ac120002",-0.05,
            EntityAttributeModifier.Operation.MULTIPLY_TOTAL)
        .withDescendant(SLOW)
        .also { regMod.add(it) }

    //attack range modifiers
    val GREATER_EXTENSION = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"greater_extension"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 2, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,"12hl65gm-908f-11ed-a1eb-0242ac120002",1.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val EXTENSION = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"extension"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 5, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,"12hl65gm-908f-11ed-a1eb-0242ac120002",0.75,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(GREATER_EXTENSION)
        .also { regMod.add(it) }
    val LESSER_EXTENSION = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"lesser_extension"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 8)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,"12hl65gm-908f-11ed-a1eb-0242ac120002",0.25,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(CHEAP_TOLL)
        .withDescendant(EXTENSION)
        .also { regMod.add(it) }
    val LESSER_STUBBY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"lesser_stubby"), EquipmentModifier.EquipmentModifierTarget.WEAPON_AND_TRINKET, 8, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,"12hl65gm-908f-11ed-a1eb-0242ac120002",-0.25,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(CHEAP_TOLL)
        .withDescendant(LESSER_EXTENSION)
        .also { regMod.add(it) }
    val STUBBY = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"stubby"), EquipmentModifier.EquipmentModifierTarget.WEAPON, 8, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            ReachEntityAttributes.ATTACK_RANGE,"12hl65gm-908f-11ed-a1eb-0242ac120002",-0.75,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(LESSER_STUBBY)
        .also { regMod.add(it) }

    //reach distance modifiers
    val GRAND_REACH = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"grand_reach"), EquipmentModifier.EquipmentModifierTarget.MINING, 2, EquipmentModifier.Rarity.EPIC)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,"16ex94hp-908f-11ed-a1eb-0242ac120002",2.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(VERY_EXPENSIVE_TOLL)
        .also { regMod.add(it) }
    val GREATER_REACH = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"greater_reach"), EquipmentModifier.EquipmentModifierTarget.MINING, 4, EquipmentModifier.Rarity.RARE)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,"16ex94hp-908f-11ed-a1eb-0242ac120002",1.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withToll(EXPENSIVE_TOLL)
        .withDescendant(GRAND_REACH)
        .also { regMod.add(it) }
    val REACH = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"reach"), EquipmentModifier.EquipmentModifierTarget.MINING, 7, EquipmentModifier.Rarity.UNCOMMON)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,"16ex94hp-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(GREATER_REACH)
        .also { regMod.add(it) }
    val LESSER_REACH = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"lesser_reach"), EquipmentModifier.EquipmentModifierTarget.MINING)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,"16ex94hp-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(REACH)
        .also { regMod.add(it) }
    val LESSER_LIMITING = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"lesser_limiting"), EquipmentModifier.EquipmentModifierTarget.MINING, 6, EquipmentModifier.Rarity.BAD)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,"16ex94hp-908f-11ed-a1eb-0242ac120002",-0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(LESSER_REACH)
        .also { regMod.add(it) }
    val LIMITING = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"limiting"), EquipmentModifier.EquipmentModifierTarget.MINING, 3, EquipmentModifier.Rarity.REALLY_BAD)
        .withAttributeModifier(
            ReachEntityAttributes.REACH,"16ex94hp-908f-11ed-a1eb-0242ac120002",-1.25,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(LESSER_LIMITING)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    //misc modifiers
    val SHIELDING = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"shielding"), TRINKET_AND_SHIELD,5,EquipmentModifier.Rarity.UNCOMMON)
        .withOnDamaged(ModifierFunctions.SHIELDING_DAMAGE_FUNCTION)
        .withToll(EXPENSIVE_TOLL)
        .also { regMod.add(it) }

    fun registerAll(){
        regMod.forEach {
            ModifierRegistry.register(it)
        }
    }

}
