package me.fzzyhmstrs.gearifiers.mixins;

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable;
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper;
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    @Shadow public abstract Item getItem();

    @Shadow public abstract int getDamage();

    @Shadow public abstract int getMaxDamage();

    @Shadow public abstract void setDamage(int damage);

    @Shadow public abstract NbtCompound getOrCreateNbt();

    @Shadow public abstract @Nullable NbtCompound getNbt();

    @Inject(method = "onCraft", at = @At("TAIL"))
    private void gearifiers_onCraftAddModifiers(World world, PlayerEntity player, int amount, CallbackInfo ci){
        if (!world.isClient && getItem() instanceof Modifiable && !GearifiersConfig.getInstance().isItemBlackListed((ItemStack) (Object) this)){
            if (!GearifiersConfig.getInstance().isScreenHandlerBlackListed(player)) {
                //LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world).random(world.random).luck(player.getLuck());
                NbtCompound nbt = getNbt();
                if (nbt == null || !nbt.getBoolean("addedViaCraft")) {
                    EquipmentModifierHelper.INSTANCE.rerollModifiers((ItemStack) (Object) this, (ServerWorld) world, player);
                    if (this.getDamage() > this.getMaxDamage()) {
                        this.setDamage(this.getMaxDamage() - 1);
                    }
                    getOrCreateNbt().putBoolean("addedViaCraft",true);
                }
            }
        }
    }

}