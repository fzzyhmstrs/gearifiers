package me.fzzyhmstrs.gearifiers.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper;
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootTable.class)
public class LootTableMixin {

    @ModifyReturnValue(method = "generateLoot (Lnet/minecraft/loot/context/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("RETURN"))
    private ObjectArrayList<ItemStack> gearifiers_generateLootAddModifiers(ObjectArrayList<ItemStack> original, LootContext context){
        original.forEach(stack -> {
            if (!GearifiersConfigNew.getInstance().isItemBlackListed(stack)) {
                EquipmentModifierHelper.INSTANCE.addRandomModifiers(stack, context);
                if (stack.getDamage() > stack.getMaxDamage()){
                    stack.setDamage(stack.getMaxDamage() - 1);
                }
            }
        });
        return original;
    }
}
