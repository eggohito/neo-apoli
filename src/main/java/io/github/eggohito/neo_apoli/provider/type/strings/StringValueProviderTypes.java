package io.github.eggohito.neo_apoli.provider.type.strings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.StringValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.strings.ConstantStringValueProvider;
import io.github.eggohito.neo_apoli.provider.custom.strings.UuidStringValueProvider;
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

public final class StringValueProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<StringValueProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.VALUE_PROVIDER_TYPE, ALIASES).comapFlatMap(StringValueProviderTypes::validate, Function.identity());
	public static final PacketCodec<RegistryByteBuf, StringValueProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.VALUE_PROVIDER_TYPE).xmap(valueProviderType -> validate(valueProviderType).getOrThrow(), Function.identity());

	public static final StringValueProviderType<ConstantStringValueProvider> CONSTANT = registerInternal("constant", ConstantStringValueProvider.CODEC, ConstantStringValueProvider.PACKET_CODEC);
	public static final StringValueProviderType<UuidStringValueProvider> UUID = registerInternal("uuid", UuidStringValueProvider.CODEC, UuidStringValueProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends StringValueProvider> StringValueProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	private static <P extends StringValueProvider> StringValueProviderType<P> registerInternal(String path, MapCodec<P> mapCodec) {
		return registerInternal(path, mapCodec, PacketCodecs.unlimitedRegistryCodec(mapCodec.codec()));
	}

	public static <P extends StringValueProvider> StringValueProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.VALUE_PROVIDER_TYPE, id, new StringValueProviderType<>(mapCodec, packetCodec));
	}

	public static <P extends StringValueProvider> StringValueProviderType<P> register(Identifier id, MapCodec<P> mapCodec) {
		return register(id, mapCodec, PacketCodecs.unlimitedRegistryCodec(mapCodec.codec()));
	}

	private static DataResult<StringValueProviderType<?>> validate(ValueProviderType<?> valueProviderType) {

		if (valueProviderType instanceof StringValueProviderType<?> stringValueProviderType) {
			return DataResult.success(stringValueProviderType);
		}

		else {
			return DataResult.error(() -> "Value provider type \"" + RegistryUtil.getId(NeoApoliRegistries.VALUE_PROVIDER_TYPE, valueProviderType) + "\" does not provide a string value!");
		}

	}

}
