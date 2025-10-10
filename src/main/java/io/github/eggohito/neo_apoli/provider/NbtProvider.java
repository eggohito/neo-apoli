package io.github.eggohito.neo_apoli.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.meta.nbt.ConstantNbtProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public abstract class NbtProvider extends ValueProvider<NbtElement> {

	public static final String TYPE_KEY = "type";
	public static final PacketCodec<RegistryByteBuf, NbtProvider> PACKET_CODEC = NbtProviderType.PACKET_CODEC.dispatch(NbtProvider::getType, NbtProviderType::packetCodec);

	public static final MapCodec<NbtProvider> MAP_CODEC = NbtProviderType.CODEC.dispatchMap(TYPE_KEY, NbtProvider::getType, NbtProviderType::mapCodec);
	public static final Codec<NbtProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(MAP_CODEC.codec(), ConstantNbtProvider.INLINE_CODEC));

	@Override
	public abstract NbtProviderType<?> getType();

	@Override
	public final NbtElement next(Context context) {
		return provideValue("NBT", context, this::impl, NbtCompound::new);
	}

	protected abstract NbtElement impl(Context context);

	@Override
	public String asDisplayString() {
		return "NBT provider with target \"" + RegistryUtil.getId(NeoApoliRegistries.NBT_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
