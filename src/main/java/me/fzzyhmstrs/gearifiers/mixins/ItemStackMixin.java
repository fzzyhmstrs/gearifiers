package me.fzzyhmstrs.gearifiers.mixins;

import me.fzzyhmstrs.fzzy_core.nbt_util.NbtKeys;
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Shadow private @Nullable NbtCompound nbt;

    @Inject(method = "onCraft", at = @At("HEAD"))
    private void gearifiers_onCraftAddModifiers(World world, PlayerEntity player, int amount, CallbackInfo ci){
        if (!world.isClient){
            LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world).random(world.random).luck(player.getLuck());
            Exception e = new Exception();
            e.printStackTrace();
            System.out.println(this.nbt);
            EquipmentModifierHelper.INSTANCE.addRandomModifiers((ItemStack) (Object) this, contextBuilder.build(LootContextTypes.EMPTY));
            System.out.println(this.nbt);
        }
    }

}
