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

	public static final WorldConditionType<AllOfWorldCondition> ALL_OF = registerMetaInternal("all_of", AllOfWorldCondition.CODEC, AllOfWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<AnyOfWorldCondition> ANY_OF = registerMetaInternal("any_of", AnyOfWorldCondition.CODEC, AnyOfWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<CompareWorldCondition> COMPARE = registerMetaInternal("compare", CompareWorldCondition.CODEC, CompareWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<CompareToRangeWorldCondition> COMPARE_TO_RANGE = registerMetaInternal("compare_to_range", CompareToRangeWorldCondition.CODEC, CompareToRangeWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<ConstantWorldCondition> CONSTANT = registerMetaInternal("constant", ConstantWorldCondition.CODEC, ConstantWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<InvertedWorldCondition> INVERTED = registerMetaInternal("inverted", InvertedWorldCondition.CODEC, InvertedWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<ReferenceWorldCondition> REFERENCE = registerMetaInternal("reference", ReferenceWorldCondition.CODEC, ReferenceWorldCondition.STREAM_CODEC);

	public static final WorldConditionType<IsInTagWorldCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagWorldCondition.CODEC, IsInTagWorldCondition.STREAM_CODEC);
	public static final WorldConditionType<IsOfWorldCondition> IS_OF = registerInternal("is_of", IsOfWorldCondition.CODEC, IsOfWorldCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends WorldCondition> WorldConditionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends WorldCondition> WorldConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends WorldCondition> WorldConditionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.WORLD_CONDITION_TYPE, id, new WorldConditionType<>(mapCodec, streamCodec));
	}

	public static <C extends WorldCondition> WorldConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(WorldConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
