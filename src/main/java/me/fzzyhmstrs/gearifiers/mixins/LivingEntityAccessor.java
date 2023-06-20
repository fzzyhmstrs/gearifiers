package me.fzzyhmstrs.gearifiers.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("lastDamageTaken")
    float getLastDamageTaken();

    @Invoker("dropLoot")
    void gearifiers_callDropLoot(DamageSource source, boolean causedByPlayer);

}
