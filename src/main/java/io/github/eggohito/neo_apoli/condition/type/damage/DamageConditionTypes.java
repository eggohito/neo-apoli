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

public class DamageConditionTypes {

	public static final DamageConditionType<AllOfDamageCondition> ALL_OF = registerInternal("all_of", AllOfDamageCondition.MAP_CODEC, AllOfDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<AnyOfDamageCondition> ANY_OF = registerInternal("any_of", AnyOfDamageCondition.MAP_CODEC, AnyOfDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<CompareDamageCondition> COMPARE = registerInternal("compare", CompareDamageCondition.MAP_CODEC, CompareDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<CompareToRangeDamageCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeDamageCondition.MAP_CODEC, CompareToRangeDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<ConstantDamageCondition> CONSTANT = registerInternal("constant", ConstantDamageCondition.MAP_CODEC, ConstantDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<DynamicDamageCondition> DYNAMIC = registerInternal("dynamic", DynamicDamageCondition.MAP_CODEC, DynamicDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<InvertedDamageCondition> INVERTED = registerInternal("inverted", InvertedDamageCondition.MAP_CODEC, InvertedDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<ReferenceDamageCondition> REFERENCE = registerInternal("reference", ReferenceDamageCondition.MAP_CODEC, ReferenceDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<TestWorldDamageCondition> TEST_WORLD = registerInternal("test_world", TestWorldDamageCondition.MAP_CODEC, TestWorldDamageCondition.STREAM_CODEC);

	public static final DamageConditionType<IsInTagDamageCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagDamageCondition.MAP_CODEC, IsInTagDamageCondition.STREAM_CODEC);
	public static final DamageConditionType<IsOfDamageCondition> IS_OF = registerInternal("is_of", IsOfDamageCondition.MAP_CODEC, IsOfDamageCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends DamageCondition> DamageConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends DamageCondition> DamageConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(DamageConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, prefixedId, new DamageConditionType<>(mapCodec, streamCodec)));
	}

}
