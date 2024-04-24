package me.fzzyhmstrs.gearifiers.modifier

import me.fzzyhmstrs.fzzy_core.modifier_util.AbstractModifierHelper
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig
import net.minecraft.util.Identifier
import me.fzzyhmstrs.gear_core.modifier_util.ConfigEquipmentModifier as ConfigEquipmentModifier1

class ConfigEquipmentModifier(
    modifierId: Identifier = AbstractModifierHelper.BLANK,
    target: EquipmentModifierTarget = EquipmentModifierTarget.NONE,
    weight: Int = 10,
    rarity: Rarity = Rarity.COMMON)
: ConfigEquipmentModifier1(
    modifierId,
    target,
    weight,
    rarity
){
    override fun isEnabled(): Boolean{
        return GearifiersConfig.getInstance().modifiers.isModifierEnabled(modifierId)
    }
}