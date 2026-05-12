package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.ContextUser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface ValueProvider extends ContextUser {

	Type<?> getType();

	interface Type<P extends ValueProvider> {

		MapCodec<P> mapCodec();

		StreamCodec<RegistryFriendlyByteBuf, P> streamCodec();

	}

}
