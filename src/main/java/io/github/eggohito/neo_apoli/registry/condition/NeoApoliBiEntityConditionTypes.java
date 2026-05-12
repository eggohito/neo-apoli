package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.bientity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliBiEntityConditionTypes {

	public static final BiEntityCondition.Type<AllOfBiEntityCondition> ALL_OF = registerInternal("all_of", AllOfBiEntityCondition.MAP_CODEC, AllOfBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<AnyOfBiEntityCondition> ANY_OF = registerInternal("any_of", AnyOfBiEntityCondition.MAP_CODEC, AnyOfBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<CompareBiEntityCondition> COMPARE = registerInternal("compare", CompareBiEntityCondition.MAP_CODEC, CompareBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<CompareToRangeBiEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeBiEntityCondition.MAP_CODEC, CompareToRangeBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<ConstantBiEntityCondition> CONSTANT = registerInternal("constant", ConstantBiEntityCondition.MAP_CODEC, ConstantBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<DynamicBiEntityCondition> DYNAMIC = registerInternal("dynamic", DynamicBiEntityCondition.MAP_CODEC, DynamicBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<InvertedBiEntityCondition> INVERTED = registerInternal("inverted", InvertedBiEntityCondition.MAP_CODEC, InvertedBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<ReferenceBiEntityCondition> REFERENCE = registerInternal("reference", ReferenceBiEntityCondition.MAP_CODEC, ReferenceBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<TestEntityBiEntityCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityBiEntityCondition.MAP_CODEC, TestEntityBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<TestWorldBiEntityCondition> TEST_WORLD = registerInternal("test_world", TestWorldBiEntityCondition.MAP_CODEC, TestWorldBiEntityCondition.STREAM_CODEC);

	public static final BiEntityCondition.Type<EqualsBiEntityCondition> EQUALS = registerInternal("equals", EqualsBiEntityCondition.MAP_CODEC, EqualsBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<IsOwnerBiEntityCondition> IS_OWNER = registerInternal("is_owner", IsOwnerBiEntityCondition.MAP_CODEC, IsOwnerBiEntityCondition.STREAM_CODEC);
	public static final BiEntityCondition.Type<SwapBiEntityCondition> SWAP = registerInternal("swap", SwapBiEntityCondition.MAP_CODEC, SwapBiEntityCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityCondition> BiEntityCondition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BiEntityCondition> BiEntityCondition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, id, new BiEntityCondition.Type<>(mapCodec, streamCodec));
	}

}
