package io.github.eggohito.neo_apoli.provider.type.bool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.NbtBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BooleanProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<BooleanProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, BooleanProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BOOLEAN_PROVIDER_TYPE);

	public static final BooleanProviderType<ConstantBooleanProvider> CONSTANT = registerInternal("constant", ConstantBooleanProvider.CODEC, ConstantBooleanProvider.PACKET_CODEC);
	public static final BooleanProviderType<NbtBooleanProvider> NBT = registerInternal("nbt", NbtBooleanProvider.CODEC, NbtBooleanProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends BooleanProvider> BooleanProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends BooleanProvider> BooleanProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, id, new BooleanProviderType<>(mapCodec, packetCodec));
	}

	public static Identifier getId(BooleanProviderType<?> type) {
		return RegistryUtil.getId(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, type);
	}

}
