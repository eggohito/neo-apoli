package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.comparison.Comparison;
import io.github.eggohito.neo_apoli.comparison.custom.*;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliComparisonTypes {

	public static final Comparison.Type<EntityComparison> ENTITY = registerInternal("entity", EntityComparison.CODEC, EntityComparison.STREAM_CODEC);
	public static final Comparison.Type<FloatComparison> FLOAT = registerInternal("float", FloatComparison.CODEC, FloatComparison.STREAM_CODEC);
	public static final Comparison.Type<IntComparison> INT = registerInternal("int", IntComparison.CODEC, IntComparison.STREAM_CODEC);
	public static final Comparison.Type<NbtComparison> NBT = registerInternal("nbt", NbtComparison.CODEC, NbtComparison.STREAM_CODEC);
	public static final Comparison.Type<StringComparison> STRING = registerInternal("string", StringComparison.CODEC, StringComparison.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends Comparison> Comparison.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends Comparison> Comparison.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.COMPARISON_TYPE, id, new Comparison.Type<>(mapCodec, streamCodec));
	}

}
