package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DefaultAttributes.class)
public interface DefaultAttributesAccessor {

	@Accessor("SUPPLIERS")
	static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getSuppliers() {
		throw new AssertionError();
	}

}
