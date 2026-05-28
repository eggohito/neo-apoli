package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistryOps.class)
public interface RegistryOpsAccessor {

	@Accessor
	RegistryOps.RegistryInfoLookup getLookupProvider();

	@Mixin(RegistryOps.HolderLookupAdapter.class)
	interface HolderLookupAdapterAccessor {

		@Accessor
		HolderLookup.Provider getLookupProvider();

	}

}
