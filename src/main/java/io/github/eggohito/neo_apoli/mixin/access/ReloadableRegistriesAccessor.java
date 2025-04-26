package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.ReloadableRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReloadableRegistries.class)
public interface ReloadableRegistriesAccessor {

	@Mixin(ReloadableRegistries.Lookup.class)
	interface LookupAccessor {

		@Accessor
		RegistryWrapper.WrapperLookup getRegistries();

	}

}
