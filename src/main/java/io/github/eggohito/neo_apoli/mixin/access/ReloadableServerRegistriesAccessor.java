package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.ReloadableServerRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReloadableServerRegistries.class)
public interface ReloadableServerRegistriesAccessor {

	@Mixin(ReloadableServerRegistries.Holder.class)
	interface HolderAccessor {

		@Accessor
		HolderLookup.Provider getRegistries();

	}

}
