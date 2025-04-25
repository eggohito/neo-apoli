package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextParameterMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ContextParameterMap.class)
public interface ContextParameterMapAccessor {

	@Accessor
	Map<ContextParameter<?>, Object> getMap();

	@Mixin(ContextParameterMap.Builder.class)
	interface BuilderAccessor {

		@Accessor
		Map<ContextParameter<?>, Object> getMap();

	}

}
