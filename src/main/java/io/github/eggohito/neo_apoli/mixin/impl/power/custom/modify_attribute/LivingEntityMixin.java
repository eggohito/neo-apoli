package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_attribute;

import io.github.eggohito.neo_apoli.impl.misc.EntityCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	@Final
	private AttributeMap attributes;

	LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	void passThisToAttributes(EntityType<?> entityType, Level level, CallbackInfo ci) {

		if (this.attributes instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity(this);
		}

	}

}
