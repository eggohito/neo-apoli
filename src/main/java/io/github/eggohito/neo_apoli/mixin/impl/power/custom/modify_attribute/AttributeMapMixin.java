package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_attribute;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.api.misc.CustomClearable;
import io.github.eggohito.neo_apoli.api.misc.EntityCache;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AttributeMap.class)
public abstract class AttributeMapMixin implements EntityCache, CustomClearable {

	@Shadow
	@Final
	private AttributeSupplier supplier;

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

	@WrapOperation(method = "getValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;getValue()D"))
	double passEntityToAttributeBeforeValue(AttributeInstance attributeInstance, Operation<Double> original) {

		if (attributeInstance instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity(this.neo_apoli$getEntity());
		}

		return original.call(attributeInstance);

	}

	@WrapOperation(method = "getValue", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier;getValue(Lnet/minecraft/core/Holder;)D"))
	double passEntityToSupplierBeforeValue(AttributeSupplier supplier, Holder<Attribute> attribute, Operation<Double> original) {

		if (supplier instanceof EntityCache entityCache) {
			entityCache.neo_apoli$setEntity(this.neo_apoli$getEntity());
		}

		return original.call(supplier, attribute);

	}

	@Inject(method = "getValue", at = @At("RETURN"))
	void cleanup(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir, @Local AttributeInstance attributeInstance) {

		if (attributeInstance instanceof CustomClearable clearable) {
			clearable.neo_apoli$clear();
		}

		if (this.supplier instanceof CustomClearable clearable) {
			clearable.neo_apoli$clear();
		}

	}

}
