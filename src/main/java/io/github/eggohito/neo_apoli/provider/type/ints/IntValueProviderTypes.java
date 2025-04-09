package io.github.eggohito.neo_apoli.provider.type.ints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.IntValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.ints.ConstantIntValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.ints.TimeIntValueProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class IntValueProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<IntValueProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.VALUE_PROVIDER_TYPE, ALIASES).comapFlatMap(IntValueProviderTypes::validate, Function.identity());
	public static final PacketCodec<RegistryByteBuf, IntValueProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.VALUE_PROVIDER_TYPE).xmap(valueProviderType -> validate(valueProviderType).getOrThrow(), Function.identity());

	public static final IntValueProviderType<ConstantIntValueProvider> CONSTANT = registerInternal("constant", ConstantIntValueProvider.CODEC, ConstantIntValueProvider.PACKET_CODEC);
	public static final IntValueProviderType<TimeIntValueProvider> TIME = registerInternal("time", TimeIntValueProvider.CODEC, TimeIntValueProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends IntValueProvider> IntValueProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	private static <P extends IntValueProvider> IntValueProviderType<P> registerInternal(String path, MapCodec<P> mapCodec) {
		return registerInternal(path, mapCodec, PacketCodecs.unlimitedRegistryCodec(mapCodec.codec()));
	}

	public static <P extends IntValueProvider> IntValueProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.VALUE_PROVIDER_TYPE, id, new IntValueProviderType<>(mapCodec, packetCodec));
	}

	public static <P extends IntValueProvider> IntValueProviderType<P> register(Identifier id, MapCodec<P> mapCodec) {
		return register(id, mapCodec, PacketCodecs.unlimitedRegistryCodec(mapCodec.codec()));
	}

	private static DataResult<IntValueProviderType<?>> validate(ValueProviderType<?> valueProviderType) {

		if (valueProviderType instanceof IntValueProviderType<?> intValueProviderType) {
			return DataResult.success(intValueProviderType);
		}

		else {
			return DataResult.error(() -> "Value provider type \"" + RegistryUtil.getId(NeoApoliRegistries.VALUE_PROVIDER_TYPE, valueProviderType) + "\" does not provide an integer value!");
		}

	}

}
