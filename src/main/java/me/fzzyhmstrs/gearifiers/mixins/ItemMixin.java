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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "onCraft", at = @At("TAIL"))
    private void gearifiers_onCraftAddModifiers(ItemStack stack, World world, PlayerEntity player, CallbackInfo ci){
        if (!world.isClient && this instanceof Modifiable && !GearifiersConfig.INSTANCE.getBlackList().isItemBlackListed((Item) (Object) this)){
            if (!GearifiersConfig.INSTANCE.getBlackList().isScreenHandlerBlackListed(player)) {
                //LootContext.Builder contextBuilder = new LootContext.Builder((ServerWorld) world).random(world.random).luck(player.getLuck());
                NbtCompound nbt = stack.getNbt();
                if (nbt == null || !nbt.getBoolean("addedViaCraft")) {
                    EquipmentModifierHelper.INSTANCE.rerollModifiers(stack, (ServerWorld) world, player);
                    if (stack.getDamage() > stack.getMaxDamage()) {
                        stack.setDamage(stack.getMaxDamage() - 1);
                    }
                    stack.getOrCreateNbt().putBoolean("addedViaCraft",true);
                }
            }
        }
    }

}
