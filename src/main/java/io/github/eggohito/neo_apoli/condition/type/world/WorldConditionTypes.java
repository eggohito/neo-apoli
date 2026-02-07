package io.github.eggohito.neo_apoli.condition.type.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.world.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class WorldConditionTypes extends ConditionTypes {

	public static final WorldConditionType<AllOfWorldCondition> ALL_OF = registerInternal("all_of", AllOfWorldCondition.MAP_CODEC, AllOfWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<AnyOfWorldCondition> ANY_OF = registerInternal("any_of", AnyOfWorldCondition.MAP_CODEC, AnyOfWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<CompareToRangeWorldCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeWorldCondition.MAP_CODEC, CompareToRangeWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<CompareWorldCondition> COMPARE = registerInternal("compare", CompareWorldCondition.MAP_CODEC, CompareWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<ConstantWorldCondition> CONSTANT = registerInternal("constant", ConstantWorldCondition.MAP_CODEC, ConstantWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<DynamicWorldCondition> DYNAMIC = registerInternal("dynamic", DynamicWorldCondition.MAP_CODEC, DynamicWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<InvertedWorldCondition> INVERTED = registerInternal("inverted", InvertedWorldCondition.MAP_CODEC, InvertedWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<ReferenceWorldCondition> REFERENCE = registerInternal("reference", ReferenceWorldCondition.MAP_CODEC, ReferenceWorldCondition.STREAM_CODEC);

	public static final WorldConditionType<DifficultyWorldCondition> DIFFICULTY = registerInternal("difficulty", DifficultyWorldCondition.MAP_CODEC, DifficultyWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<IsInTagWorldCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagWorldCondition.MAP_CODEC, IsInTagWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<IsOfWorldCondition> IS_OF = registerInternal("is_of", IsOfWorldCondition.MAP_CODEC, IsOfWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<IsRainingAtWorldCondition> IS_RAINING_AT = registerInternal("is_raining_at", IsRainingAtWorldCondition.MAP_CODEC, IsRainingAtWorldCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends WorldCondition> WorldConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends WorldCondition> WorldConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(WorldConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.WORLD_CONDITION_TYPE, prefixedId, new WorldConditionType<>(mapCodec, streamCodec)));
	}

}
