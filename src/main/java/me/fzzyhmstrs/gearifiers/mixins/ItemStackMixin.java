package me.fzzyhmstrs.gearifiers.mixins;

import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "onCraft", at = @At("HEAD"))
    private void gearifiers_onCraftAddModifiers(World world, PlayerEntity player, int amount, CallbackInfo ci){
        if (!world.isClient){
            LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world).random(world.random).luck(player.getLuck());
            EquipmentModifierHelper.INSTANCE.addRandomModifiers((ItemStack) (Object) this, contextBuilder.build(LootContextTypes.GENERIC));
        }
    }

}
