package me.fzzyhmstrs.gearifiers.mixins;

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable;
import me.fzzyhmstrs.fzzy_core.nbt_util.Nbt;
import me.fzzyhmstrs.fzzy_core.nbt_util.NbtKeys;
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper;
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
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
public abstract class ItemStackMixin {

    @Shadow private @Nullable NbtCompound nbt;

    @Shadow public abstract Item getItem();

    @Inject(method = "onCraft", at = @At("HEAD"))
    private void gearifiers_onCraftAddModifiers(World world, PlayerEntity player, int amount, CallbackInfo ci){
        if (!world.isClient && getItem() instanceof Modifiable && !GearifiersConfig.INSTANCE.getBlackList().isItemBlackListed((ItemStack) (Object) this)){
            LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world).random(world.random).luck(player.getLuck());
            if (-1 != Nbt.INSTANCE.getItemStackId((ItemStack) (Object) this) && nbt != null){
                nbt.remove(NbtKeys.ITEM_STACK_ID.str());
            }
            EquipmentModifierHelper.INSTANCE.addRandomModifiers((ItemStack) (Object) this, contextBuilder.build(LootContextTypes.EMPTY));
        }
    }

}
