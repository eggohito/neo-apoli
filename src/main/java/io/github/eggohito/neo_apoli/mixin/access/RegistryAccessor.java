package io.github.eggohito.neo_apoli.mixin.access;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Registry.class)
public interface RegistryAccessor {

	@Invoker
	<T> DataResult<Holder.Reference<T>> callSafeCastToReference(Holder<T> entry);

}
