package io.github.eggohito.neo_apoli.condition.type.effect;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.effect.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class EffectConditionTypes extends ConditionTypes {

	public static final EffectConditionType<AllOfEffectCondition> ALL_OF = registerInternal("all_of", AllOfEffectCondition.MAP_CODEC, AllOfEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<AnyOfEffectCondition> ANY_OF = registerInternal("any_of", AnyOfEffectCondition.MAP_CODEC, AnyOfEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<CompareEffectCondition> COMPARE = registerInternal("compare", CompareEffectCondition.MAP_CODEC, CompareEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<CompareToRangeEffectCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeEffectCondition.MAP_CODEC, CompareToRangeEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<ConstantEffectCondition> CONSTANT = registerInternal("constant", ConstantEffectCondition.MAP_CODEC, ConstantEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<DynamicEffectCondition> DYNAMIC = registerInternal("dynamic", DynamicEffectCondition.MAP_CODEC, DynamicEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<InvertedEffectCondition> INVERTED = registerInternal("inverted", InvertedEffectCondition.MAP_CODEC, InvertedEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<ReferenceEffectCondition> REFERENCE = registerInternal("reference", ReferenceEffectCondition.MAP_CODEC, ReferenceEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<TestWorldEffectCondition> TEST_WORLD = registerInternal("test_world", TestWorldEffectCondition.MAP_CODEC, TestWorldEffectCondition.STREAM_CODEC);

	public static final EffectConditionType<IsInTagEffectCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagEffectCondition.MAP_CODEC, IsInTagEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<IsOfEffectCondition> IS_OF = registerInternal("is_of", IsOfEffectCondition.MAP_CODEC, IsOfEffectCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EffectCondition> EffectConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends EffectCondition> EffectConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(EffectConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.EFFECT_CONDITION_TYPE, prefixedId, new EffectConditionType<>(mapCodec, streamCodec)));
	}

}
