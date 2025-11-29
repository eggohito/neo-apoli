package io.github.eggohito.neo_apoli.util.comparison.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import io.github.eggohito.neo_apoli.util.comparison.NbtComparison;
import io.github.eggohito.neo_apoli.util.comparison.NumberComparison;
import io.github.eggohito.neo_apoli.util.comparison.StringComparison;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ComparisonTypes {

	public static final ComparisonType<NbtComparison> NBT = registerInternal("nbt", NbtComparison.CODEC, NbtComparison.STREAM_CODEC);
	public static final ComparisonType<NumberComparison> NUMBER = registerInternal("number", NumberComparison.CODEC, NumberComparison.STREAM_CODEC);
	public static final ComparisonType<StringComparison> STRING = registerInternal("string", StringComparison.CODEC, StringComparison.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends Comparison> ComparisonType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends Comparison> ComparisonType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.COMPARISON_TYPE, id, new ComparisonType<>(mapCodec, packetCodec));
	}

}
