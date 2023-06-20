package me.fzzyhmstrs.gearifiers.mixins;

import me.fzzyhmstrs.fzzy_core.interfaces.Modifiable;
import me.fzzyhmstrs.gear_core.modifier_util.EquipmentModifierHelper;
import me.fzzyhmstrs.gearifiers.config.GearifiersConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Shadow public World world;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;", at = @At("HEAD"))
    private void gearifiers_dropStackAddModifiers(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir){
        if (!world.isClient && stack.getItem() instanceof Modifiable && !GearifiersConfig.INSTANCE.getBlackList().isItemBlackListed(stack)){
            LootContextParameterSet.Builder parameters = new LootContextParameterSet.Builder((ServerWorld) world);
            if (((Entity)(Object)this) instanceof PlayerEntity player){
                parameters.luck(player.getLuck());
            }
            LootContextParameterSet finalParameters = parameters.build(LootContextTypes.EMPTY);
            long seed = world.random.nextLong();
            if (seed == 0L) seed = 1L;
            LootContext.Builder contextBuilder = new LootContext.Builder(finalParameters).random(seed);
            EquipmentModifierHelper.INSTANCE.addRandomModifiers(stack, contextBuilder.build(null));
            if (stack.getDamage() > stack.getMaxDamage()){
                stack.setDamage(stack.getMaxDamage() - 1);
            }
        }
    }

}
