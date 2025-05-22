package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.string.ConstantStringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public interface StringProvider extends ContextAware {

	String TYPE_KEY = "type";
	PacketCodec<RegistryByteBuf, StringProvider> PACKET_CODEC = StringProviderTypes.PACKET_CODEC.dispatch(StringProvider::getType, StringProviderType::packetCodec);

	MapCodec<StringProvider> MAP_CODEC = StringProviderTypes.CODEC.dispatchMap(TYPE_KEY, StringProvider::getType, StringProviderType::mapCodec);
	Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantStringProvider.INLINE_CODEC));

	StringProviderType<?> getType();

	String stringValue(Context context);

	@Override
	default String asDisplayString() {
		return "String provider (with type \"" + RegistryUtil.getId(NeoApoliRegistries.STRING_PROVIDER_TYPE, this.getType()) + "\")";
	}

}
