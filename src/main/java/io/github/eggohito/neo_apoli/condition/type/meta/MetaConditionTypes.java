package io.github.eggohito.neo_apoli.condition.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.*;
import io.github.eggohito.neo_apoli.condition.custom.meta.MetaCondition;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class MetaConditionTypes extends ConditionTypes {

	public static final MetaConditionType<AllOfCondition> ALL_OF = registerInternal("all_of", AllOfCondition.CODEC, AllOfCondition.STREAM_CODEC);
	public static final MetaConditionType<AnyOfCondition> ANY_OF = registerInternal("any_of", AnyOfCondition.CODEC, AnyOfCondition.STREAM_CODEC);
	public static final MetaConditionType<CompareCondition> COMPARE = registerInternal("compare", CompareCondition.CODEC, CompareCondition.STREAM_CODEC);
	public static final MetaConditionType<CompareToRangeCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeCondition.CODEC, CompareToRangeCondition.STREAM_CODEC);
	public static final MetaConditionType<ConstantCondition> CONSTANT = registerInternal("constant", ConstantCondition.CODEC, ConstantCondition.STREAM_CODEC);
	public static final MetaConditionType<DynamicCondition> DYNAMIC = registerInternal("dynamic", DynamicCondition.CODEC, DynamicCondition.STREAM_CODEC);
	public static final MetaConditionType<InvertedCondition> INVERTED = registerInternal("inverted", InvertedCondition.CODEC, InvertedCondition.STREAM_CODEC);
	public static final MetaConditionType<ReferenceCondition> REFERENCE = registerInternal("reference", ReferenceCondition.CODEC, ReferenceCondition.STREAM_CODEC);
	public static final MetaConditionType<TestEntityCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityCondition.CODEC, TestEntityCondition.STREAM_CODEC);
	public static final MetaConditionType<TestWorldCondition> TEST_WORLD = registerInternal("test_world", TestWorldCondition.CODEC, TestWorldCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends MetaCondition> MetaConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends MetaCondition> MetaConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(id, new MetaConditionType<>(mapCodec, packetCodec));
	}

}
