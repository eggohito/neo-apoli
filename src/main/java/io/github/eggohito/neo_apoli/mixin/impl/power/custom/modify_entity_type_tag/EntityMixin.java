package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_entity_type_tag;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.api.misc.EntityCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public abstract EntityType<?> getType();

	@ModifyReturnValue(method = "getType", at = @At("RETURN"))
	EntityType<?> cacheEntityToType(EntityType<?> original) {

		if (original instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity((Entity) (Object) this);
		}

		return original;

	}

	@ModifyExpressionValue(method = "causeFallDamage", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/Entity;type:Lnet/minecraft/world/entity/EntityType;", opcode = Opcodes.GETFIELD))
	EntityType<?> fixTypeCalls(EntityType<?> original) {
		return this.getType();
	}

}
