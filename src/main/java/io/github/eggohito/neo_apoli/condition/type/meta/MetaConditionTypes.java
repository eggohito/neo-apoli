package io.github.eggohito.neo_apoli.condition.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.meta.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class MetaConditionTypes extends ConditionTypes {

	public static final MetaConditionType<AllOfMetaCondition> ALL_OF = registerInternal("all_of", AllOfMetaCondition.MAP_CODEC, AllOfMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<AnyOfMetaCondition> ANY_OF = registerInternal("any_of", AnyOfMetaCondition.MAP_CODEC, AnyOfMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<CompareMetaCondition> COMPARE = registerInternal("compare", CompareMetaCondition.MAP_CODEC, CompareMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<CompareToRangeMetaCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeMetaCondition.MAP_CODEC, CompareToRangeMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<ConstantMetaCondition> CONSTANT = registerInternal("constant", ConstantMetaCondition.MAP_CODEC, ConstantMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<DynamicMetaCondition> DYNAMIC = registerInternal("dynamic", DynamicMetaCondition.MAP_CODEC, DynamicMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<InvertedMetaCondition> INVERTED = registerInternal("inverted", InvertedMetaCondition.MAP_CODEC, InvertedMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<ReferenceMetaCondition> REFERENCE = registerInternal("reference", ReferenceMetaCondition.MAP_CODEC, ReferenceMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<TestEntityMetaCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityMetaCondition.MAP_CODEC, TestEntityMetaCondition.STREAM_CODEC);
	public static final MetaConditionType<TestWorldMetaCondition> TEST_WORLD = registerInternal("test_world", TestWorldMetaCondition.MAP_CODEC, TestWorldMetaCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends MetaCondition> MetaConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends MetaCondition> MetaConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(id, new MetaConditionType<>(mapCodec, packetCodec));
	}

}
