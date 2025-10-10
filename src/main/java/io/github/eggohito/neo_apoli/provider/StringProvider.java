package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.string.ConstantStringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public abstract class StringProvider extends ValueProvider<String> {

	public static final String TYPE_KEY = "type";
	public static final PacketCodec<RegistryByteBuf, StringProvider> PACKET_CODEC = StringProviderType.PACKET_CODEC.dispatch(StringProvider::getType, StringProviderType::packetCodec);

	public static final MapCodec<StringProvider> MAP_CODEC = StringProviderType.CODEC.dispatchMap(TYPE_KEY, StringProvider::getType, StringProviderType::mapCodec);
	public static final Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantStringProvider.INLINE_CODEC));

	@Override
	public abstract StringProviderType<?> getType();

	@Override
	public final String next(Context context) {
		return provideValue("string", context, this::impl, () -> "");
	}

	protected abstract String impl(Context context);

	@Override
	public String asDisplayString() {
		return "String provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.STRING_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
