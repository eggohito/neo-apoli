package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.damage.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DamageConditionTypes extends ConditionTypes {

	public static final DamageConditionType<AllOfDamageCondition> ALL_OF = registerMetaInternal("all_of", AllOfDamageCondition.CODEC, AllOfDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<AnyOfDamageCondition> ANY_OF = registerMetaInternal("any_of", AnyOfDamageCondition.CODEC, AnyOfDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<CompareDamageCondition> COMPARE = registerMetaInternal("compare", CompareDamageCondition.CODEC, CompareDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<CompareToRangeDamageCondition> COMPARE_TO_RANGE = registerMetaInternal("compare_to_range", CompareToRangeDamageCondition.CODEC, CompareToRangeDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<ConstantDamageCondition> CONSTANT = registerMetaInternal("constant", ConstantDamageCondition.CODEC, ConstantDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<InvertedDamageCondition> INVERTED = registerMetaInternal("inverted", InvertedDamageCondition.CODEC, InvertedDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<ReferenceDamageCondition> REFERENCE = registerMetaInternal("reference", ReferenceDamageCondition.CODEC, ReferenceDamageCondition.STREAM_CODEC);

	public static final DamageConditionType<IsInTagDamageCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagDamageCondition.CODEC, IsInTagDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<IsOfDamageCondition> IS_OF = registerInternal("is_of", IsOfDamageCondition.CODEC, IsOfDamageCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends DamageCondition> DamageConditionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends DamageCondition> DamageConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends DamageCondition> DamageConditionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, id, new DamageConditionType<>(mapCodec, streamCodec));
	}

	public static <C extends DamageCondition> DamageConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(DamageConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
