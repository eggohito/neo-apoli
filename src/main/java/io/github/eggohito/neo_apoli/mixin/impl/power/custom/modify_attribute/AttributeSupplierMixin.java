package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_attribute;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.impl.misc.CustomClearable;
import io.github.eggohito.neo_apoli.impl.misc.EntityCache;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttributeSupplier.class)
public abstract class AttributeSupplierMixin implements EntityCache, CustomClearable {

	@Unique
	private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

	@Override
	public @Nullable Entity neo_apoli$getEntity() {
		return neo_apoli$entity.get();
	}

	@Override
	public void neo_apoli$setEntity(@Nullable Entity entity) {

		if (entity == null) {
			this.neo_apoli$entity.remove();
		}

		else {
			this.neo_apoli$entity.set(entity);
		}

	}

	@Override
	public void neo_apoli$clear() {
		this.neo_apoli$entity.remove();
	}

	@ModifyReturnValue(method = "getAttributeInstance", at = @At("RETURN"))
	AttributeInstance passEntityToAttribute(AttributeInstance original) {

		if (original instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity(this.neo_apoli$getEntity());
		}

		return original;

	}

}
