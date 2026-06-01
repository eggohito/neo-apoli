package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.ContextUser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface ValueProvider extends ContextUser {

	@NotNull
	Type<?> getType();

	interface Type<P extends ValueProvider> {

		MapCodec<P> mapCodec();

		StreamCodec<RegistryFriendlyByteBuf, P> streamCodec();

	}

}
