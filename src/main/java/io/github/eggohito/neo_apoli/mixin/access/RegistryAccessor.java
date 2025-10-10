package io.github.eggohito.neo_apoli.mixin.access;

import com.mojang.serialization.DataResult;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Registry.class)
public interface RegistryAccessor {

	@Invoker
	<T> DataResult<RegistryEntry.Reference<T>> callValidateReference(RegistryEntry<T> entry);

}
