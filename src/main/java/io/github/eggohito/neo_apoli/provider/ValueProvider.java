package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import net.minecraft.loot.context.LootContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface ValueProvider<T> extends LootContextAware {

	T get(ValueProviderContext context);

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
