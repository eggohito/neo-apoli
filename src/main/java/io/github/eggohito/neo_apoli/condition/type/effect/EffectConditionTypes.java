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

	public static final EffectConditionType<AllOfEffectCondition> ALL_OF = registerMetaInternal("all_of", AllOfEffectCondition.CODEC, AllOfEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<AnyOfEffectCondition> ANY_OF = registerMetaInternal("any_of", AnyOfEffectCondition.CODEC, AnyOfEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<CompareEffectCondition> COMPARE = registerMetaInternal("compare", CompareEffectCondition.CODEC, CompareEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<CompareToRangeEffectCondition> COMPARE_TO_RANGE = registerMetaInternal("compare_to_range", CompareToRangeEffectCondition.CODEC, CompareToRangeEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<ConstantEffectCondition> CONSTANT = registerMetaInternal("constant", ConstantEffectCondition.CODEC, ConstantEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<InvertedEffectCondition> INVERTED = registerMetaInternal("inverted", InvertedEffectCondition.CODEC, InvertedEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<ReferenceEffectCondition> REFERENCE = registerMetaInternal("reference", ReferenceEffectCondition.CODEC, ReferenceEffectCondition.STREAM_CODEC);

	public static final EffectConditionType<IsInTagEffectCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagEffectCondition.CODEC, IsInTagEffectCondition.STREAM_CODEC);
	public static final EffectConditionType<IsOfEffectCondition> IS_OF = registerInternal("is_of", IsOfEffectCondition.CODEC, IsOfEffectCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EffectCondition> EffectConditionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends EffectCondition> EffectConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends EffectCondition> EffectConditionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.EFFECT_CONDITION_TYPE, id, new EffectConditionType<>(mapCodec, streamCodec));
	}

	public static <C extends EffectCondition> EffectConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(EffectConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
