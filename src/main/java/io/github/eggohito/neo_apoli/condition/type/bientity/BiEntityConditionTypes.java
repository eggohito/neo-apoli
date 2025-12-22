package io.github.eggohito.neo_apoli.condition.type.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.bientity.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BiEntityConditionTypes extends ConditionTypes {

	public static final BiEntityConditionType<AllOfBiEntityCondition> ALL_OF = registerInternal("all_of", AllOfBiEntityCondition.CODEC, AllOfBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<AnyOfBiEntityCondition> ANY_OF = registerInternal("any_of", AnyOfBiEntityCondition.CODEC, AnyOfBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<CompareBiEntityCondition> COMPARE = registerInternal("compare", CompareBiEntityCondition.CODEC, CompareBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<CompareToRangeBiEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeBiEntityCondition.CODEC, CompareToRangeBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<ConstantBiEntityCondition> CONSTANT = registerInternal("constant", ConstantBiEntityCondition.CODEC, ConstantBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<DynamicBiEntityCondition> DYNAMIC = registerInternal("dynamic", DynamicBiEntityCondition.CODEC, DynamicBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<InvertedBiEntityCondition> INVERTED = registerInternal("inverted", InvertedBiEntityCondition.CODEC, InvertedBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<ReferenceBiEntityCondition> REFERENCE = registerInternal("reference", ReferenceBiEntityCondition.CODEC, ReferenceBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<TestEntityBiEntityCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityBiEntityCondition.CODEC, TestEntityBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<TestWorldBiEntityCondition> TEST_WORLD = registerInternal("test_world", TestWorldBiEntityCondition.CODEC, TestWorldBiEntityCondition.STREAM_CODEC);

	public static final BiEntityConditionType<EqualsBiEntityCondition> EQUALS = registerInternal("equals", EqualsBiEntityCondition.CODEC, EqualsBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<IsOwnerBiEntityCondition> IS_OWNER = registerInternal("is_owner", IsOwnerBiEntityCondition.CODEC, IsOwnerBiEntityCondition.STREAM_CODEC);
	public static final BiEntityConditionType<SwapBiEntityCondition> SWAP = registerInternal("swap", SwapBiEntityCondition.CODEC, SwapBiEntityCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityCondition> BiEntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BiEntityCondition> BiEntityConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(BiEntityConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, prefixedId, new BiEntityConditionType<>(mapCodec, streamCodec)));
	}

}
