package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEffectInstance.class)
public interface MobEffectInstanceAccessor {

	@Nullable
	@Accessor
	MobEffectInstance getHiddenEffect();

}
