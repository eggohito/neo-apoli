package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.string.ConstantStringProvider;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface StringProvider extends ValueProvider<String> {

	String TYPE_KEY = "type";
	PacketCodec<RegistryByteBuf, StringProvider> PACKET_CODEC = StringProviderTypes.PACKET_CODEC.dispatch(StringProvider::getType, Type::packetCodec);

	MapCodec<StringProvider> MAP_CODEC = StringProviderTypes.CODEC.dispatchMap(TYPE_KEY, StringProvider::getType, Type::mapCodec);
	Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> Codec.withAlternative(MAP_CODEC.codec(), ConstantStringProvider.INLINE_CODEC));

	@Override
	Type<?> getType();

	@Override
	String get(Context context);

	@Override
	default String asDisplayString() {
		return "String provider (with type \"" + RegistryUtil.getId(NeoApoliRegistries.STRING_PROVIDER_TYPE, this.getType()) + "\")";
	}

	record Type<P extends StringProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProvider.Type<P> {

	}

}
