package io.github.eggohito.neo_apoli.mixin.impl.power.custom.modify_attribute;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.api.misc.CustomClearable;
import io.github.eggohito.neo_apoli.api.misc.EntityCache;
import io.github.eggohito.neo_apoli.power.custom.ModifyAttributePower;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttributeInstance.class)
public abstract class AttributeInstanceMixin implements EntityCache, CustomClearable {

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

	//  TODO:   Find a way to optimize this section. Ideally, the value should only be modified on calculation but
	//          that wouldn't really suffice when conditions and/or value providers are used.
	@ModifyReturnValue(method = "getValue", at = @At("RETURN"))
	double modifyAttribute(double original) {
		return ModifyAttributePower.modify((AttributeInstance) (Object) this, original);
	}

}
