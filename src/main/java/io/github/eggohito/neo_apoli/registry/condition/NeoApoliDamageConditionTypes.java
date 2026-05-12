package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.damage.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliDamageConditionTypes {

	public static final DamageCondition.Type<AllOfDamageCondition> ALL_OF = registerInternal("all_of", AllOfDamageCondition.MAP_CODEC, AllOfDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<AnyOfDamageCondition> ANY_OF = registerInternal("any_of", AnyOfDamageCondition.MAP_CODEC, AnyOfDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<CompareDamageCondition> COMPARE = registerInternal("compare", CompareDamageCondition.MAP_CODEC, CompareDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<CompareToRangeDamageCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeDamageCondition.MAP_CODEC, CompareToRangeDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<ConstantDamageCondition> CONSTANT = registerInternal("constant", ConstantDamageCondition.MAP_CODEC, ConstantDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<DynamicDamageCondition> DYNAMIC = registerInternal("dynamic", DynamicDamageCondition.MAP_CODEC, DynamicDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<InvertedDamageCondition> INVERTED = registerInternal("inverted", InvertedDamageCondition.MAP_CODEC, InvertedDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<ReferenceDamageCondition> REFERENCE = registerInternal("reference", ReferenceDamageCondition.MAP_CODEC, ReferenceDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<TestWorldDamageCondition> TEST_WORLD = registerInternal("test_world", TestWorldDamageCondition.MAP_CODEC, TestWorldDamageCondition.STREAM_CODEC);

	public static final DamageCondition.Type<IsInTagDamageCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagDamageCondition.MAP_CODEC, IsInTagDamageCondition.STREAM_CODEC);
	public static final DamageCondition.Type<IsOfDamageCondition> IS_OF = registerInternal("is_of", IsOfDamageCondition.MAP_CODEC, IsOfDamageCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends DamageCondition> DamageCondition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends DamageCondition> DamageCondition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, id, new DamageCondition.Type<>(mapCodec, streamCodec));
	}

}
