package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.type.bool.BooleanProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public abstract class BooleanProvider extends ValueProvider<Boolean> {

	public static final String TYPE_KEY = "type";
	public static final PacketCodec<RegistryByteBuf, BooleanProvider> PACKET_CODEC = BooleanProviderType.PACKET_CODEC.dispatch(BooleanProvider::getType, BooleanProviderType::packetCodec);

	public static final MapCodec<BooleanProvider> MAP_CODEC = BooleanProviderType.CODEC.dispatchMap(TYPE_KEY, BooleanProvider::getType, BooleanProviderType::mapCodec);
	public static final Codec<BooleanProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantBooleanProvider.INLINE_CODEC));

	@Override
	public abstract BooleanProviderType<?> getType();

	@Override
	public final Boolean next(Context context) {
		return provideValue("boolean", context, this::impl, () -> false);
	}

	@Override
	public String asDisplayString() {
		return "Boolean provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, this.getType()) + "\"";
	}

	protected abstract boolean impl(Context context);

}
