package io.github.eggohito.neo_apoli.provider.type.doubles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.DoubleValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.doubles.AttributeDoubleValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.doubles.ConstantDoubleValueProvider;
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

public final class DoubleValueProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<DoubleValueProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.VALUE_PROVIDER_TYPE, ALIASES).comapFlatMap(DoubleValueProviderTypes::validate, Function.identity());
	public static final PacketCodec<RegistryByteBuf, DoubleValueProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.VALUE_PROVIDER_TYPE).xmap(providerType -> validate(providerType).getOrThrow(), Function.identity());

	public static final DoubleValueProviderType<ConstantDoubleValueProvider> CONSTANT = registerInternal("constant", ConstantDoubleValueProvider.CODEC, ConstantDoubleValueProvider.PACKET_CODEC);
	public static final DoubleValueProviderType<AttributeDoubleValueProvider> ATTRIBUTE = registerInternal("attribute", AttributeDoubleValueProvider.CODEC, AttributeDoubleValueProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends DoubleValueProvider> DoubleValueProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	private static <P extends DoubleValueProvider> DoubleValueProviderType<P> registerInternal(String path, MapCodec<P> mapCodec) {
		return registerInternal(path, mapCodec, PacketCodecs.unlimitedRegistryCodec(mapCodec.codec()));
	}

	public static <P extends DoubleValueProvider> DoubleValueProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.VALUE_PROVIDER_TYPE, id, new DoubleValueProviderType<>(mapCodec, packetCodec));
	}

	public static <P extends DoubleValueProvider> DoubleValueProviderType<P> register(Identifier id, MapCodec<P> mapCodec) {
		return register(id, mapCodec, PacketCodecs.unlimitedRegistryCodec(mapCodec.codec()));
	}

	private static DataResult<DoubleValueProviderType<?>> validate(ValueProviderType<?> valueProviderType) {

		if (valueProviderType instanceof DoubleValueProviderType<?> doubleValueProviderType) {
			return DataResult.success(doubleValueProviderType);
		}

		else {
			return DataResult.error(() -> "Value provider type \"" + RegistryUtil.getId(NeoApoliRegistries.VALUE_PROVIDER_TYPE, valueProviderType) + "\" does not provide a double value!");
		}

	}

}
