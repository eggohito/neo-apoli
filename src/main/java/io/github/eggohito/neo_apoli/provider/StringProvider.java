package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.string.ConstantStringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public abstract class StringProvider implements ContextAware, StringDisplayable {

	public static final String TYPE_KEY = "type";
	public static final PacketCodec<RegistryByteBuf, StringProvider> PACKET_CODEC = StringProviderTypes.PACKET_CODEC.dispatch(StringProvider::getType, StringProviderType::packetCodec);

	public static final MapCodec<StringProvider> MAP_CODEC = StringProviderTypes.CODEC.dispatchMap(TYPE_KEY, StringProvider::getType, StringProviderType::mapCodec);
	public static final Codec<StringProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantStringProvider.INLINE_CODEC));

	public abstract StringProviderType<?> getType();

	public final String stringValue(Context context) {
		return MiscUtil.provideValue("string", context, this::stringImpl);
	}

	protected abstract String stringImpl(Context context);

	@Override
	public String asDisplayString() {
		return "String provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.STRING_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
