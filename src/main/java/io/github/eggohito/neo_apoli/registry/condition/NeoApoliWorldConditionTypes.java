package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.world.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliWorldConditionTypes {

	public static final WorldCondition.Type<AllOfWorldCondition> ALL_OF = registerInternal("all_of", AllOfWorldCondition.MAP_CODEC, AllOfWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<AnyOfWorldCondition> ANY_OF = registerInternal("any_of", AnyOfWorldCondition.MAP_CODEC, AnyOfWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<CompareToRangeWorldCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeWorldCondition.MAP_CODEC, CompareToRangeWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<CompareWorldCondition> COMPARE = registerInternal("compare", CompareWorldCondition.MAP_CODEC, CompareWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<ConstantWorldCondition> CONSTANT = registerInternal("constant", ConstantWorldCondition.MAP_CODEC, ConstantWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<DynamicWorldCondition> DYNAMIC = registerInternal("dynamic", DynamicWorldCondition.MAP_CODEC, DynamicWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<InvertedWorldCondition> INVERTED = registerInternal("inverted", InvertedWorldCondition.MAP_CODEC, InvertedWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<ReferenceWorldCondition> REFERENCE = registerInternal("reference", ReferenceWorldCondition.MAP_CODEC, ReferenceWorldCondition.STREAM_CODEC);

	public static final WorldCondition.Type<DifficultyWorldCondition> DIFFICULTY = registerInternal("difficulty", DifficultyWorldCondition.MAP_CODEC, DifficultyWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<IsExposedToSkyWorldCondition> IS_EXPOSED_TO_SKY = registerInternal("is_exposed_to_sky", IsExposedToSkyWorldCondition.MAP_CODEC, IsExposedToSkyWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<IsInTagWorldCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagWorldCondition.MAP_CODEC, IsInTagWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<IsOfWorldCondition> IS_OF = registerInternal("is_of", IsOfWorldCondition.MAP_CODEC, IsOfWorldCondition.STREAM_CODEC);
	public static final WorldCondition.Type<IsRainingAtWorldCondition> IS_RAINING_AT = registerInternal("is_raining_at", IsRainingAtWorldCondition.MAP_CODEC, IsRainingAtWorldCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends WorldCondition> WorldCondition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends WorldCondition> WorldCondition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.WORLD_CONDITION_TYPE, id, new WorldCondition.Type<>(mapCodec, streamCodec));
	}

}
