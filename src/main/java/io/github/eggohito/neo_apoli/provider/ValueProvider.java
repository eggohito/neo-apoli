package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface ValueProvider<T> extends ContextAware {

	T get(Context context);

	Type<?> getType();

	interface Type<P extends ValueProvider<?>> {

		MapCodec<P> mapCodec();

		PacketCodec<RegistryByteBuf, P> packetCodec();

		static <P extends ValueProvider<?>> Type<P> create(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
			return new Type<>() {

				@Override
				public MapCodec<P> mapCodec() {
					return mapCodec;
				}

				@Override
				public PacketCodec<RegistryByteBuf, P> packetCodec() {
					return packetCodec;
				}

			};
		}

	}

}
