package io.github.eggohito.neo_apoli.util.comparison.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.comparison.NbtComparison;
import io.github.eggohito.neo_apoli.util.comparison.NumberComparison;
import io.github.eggohito.neo_apoli.util.comparison.StringComparison;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ComparisonTypes {

	public static final Codec<ComparisonType<?>> CODEC = NeoApoliRegistries.COMPARISON_TYPE.getCodec();
	public static final PacketCodec<RegistryByteBuf, ComparisonType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.COMPARISON_TYPE);

	public static final ComparisonType<NbtComparison> NBT = registerInternal("nbt", NbtComparison.CODEC, NbtComparison.PACKET_CODEC);
	public static final ComparisonType<NumberComparison> NUMBER = registerInternal("number", NumberComparison.CODEC, NumberComparison.PACKET_CODEC);
	public static final ComparisonType<StringComparison> STRING = registerInternal("string", StringComparison.CODEC, StringComparison.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends Comparison> ComparisonType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends Comparison> ComparisonType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.COMPARISON_TYPE, id, new ComparisonType<>(mapCodec, packetCodec));
	}

}
