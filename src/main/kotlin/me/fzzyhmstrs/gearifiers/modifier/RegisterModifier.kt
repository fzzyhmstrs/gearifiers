package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.amethyst_core.modifier_util.AbstractModifier
import me.fzzyhmstrs.amethyst_core.modifier_util.EquipmentModifier
import me.fzzyhmstrs.gearifiers.Gearifiers
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.util.Identifier

object RegisterModifier {

    private val regMod: MutableList<AbstractModifier<*>> = mutableListOf()
    //basic damage modification for weapons
    val UNGODLY_SHARP = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"ungodly_sharp"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",3.0,
            EntityAttributeModifier.Operation.ADDITION).also { regMod.add(it) }
    val RAZOR_SHARP = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"razor_sharp"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(UNGODLY_SHARP)
        .also { regMod.add(it) }
    val KEEN = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"keen"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",1.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(RAZOR_SHARP)
        .also { regMod.add(it) }
    val HONED = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"honed"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(KEEN)
        .also { regMod.add(it) }
    val SHARP = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"sharp"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(HONED)
        .also { regMod.add(it) }
    val DULL = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"dull"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",-0.5,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(SHARP)
        .also { regMod.add(it) }
    val BLUNT = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"blunt"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",-1.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(DULL)
        .also { regMod.add(it) }
    val USELESS = EquipmentModifier(Identifier(Gearifiers.MOD_ID,"useless"))
        .withAttributeModifier(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,"32tl59oa-908f-11ed-a1eb-0242ac120002",-2.0,
            EntityAttributeModifier.Operation.ADDITION)
        .withDescendant(BLUNT)
        .also { regMod.add(it) }








    fun registerAll(){


    }

}