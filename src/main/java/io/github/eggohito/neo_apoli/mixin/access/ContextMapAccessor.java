package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ContextMap.class)
public interface ContextMapAccessor {

	@Accessor
	Map<ContextKey<?>, Object> getParams();

	@Mixin(ContextMap.Builder.class)
	interface BuilderAccessor {

		@Accessor
		Map<ContextKey<?>, Object> getParams();

	}

}
