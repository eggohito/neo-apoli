package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.block.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliBlockConditionTypes {

	public static final BlockCondition.Type<AllOfBlockCondition> ALL_OF = registerInternal("all_of", AllOfBlockCondition.MAP_CODEC, AllOfBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<AnyOfBlockCondition> ANY_OF = registerInternal("any_of", AnyOfBlockCondition.MAP_CODEC, AnyOfBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<CompareBlockCondition> COMPARE = registerInternal("compare", CompareBlockCondition.MAP_CODEC, CompareBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<CompareToRangeBlockCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeBlockCondition.MAP_CODEC, CompareToRangeBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<ConstantBlockCondition> CONSTANT = registerInternal("constant", ConstantBlockCondition.MAP_CODEC, ConstantBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<DynamicBlockCondition> DYNAMIC = registerInternal("dynamic", DynamicBlockCondition.MAP_CODEC, DynamicBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<InvertedBlockCondition> INVERTED = registerInternal("inverted", InvertedBlockCondition.MAP_CODEC, InvertedBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<OffsetBlockCondition> OFFSET = registerInternal("offset", OffsetBlockCondition.MAP_CODEC, OffsetBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<ReferenceBlockCondition> REFERENCE = registerInternal("reference", ReferenceBlockCondition.MAP_CODEC, ReferenceBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<TestWorldBlockCondition> TEST_WORLD = registerInternal("test_world", TestWorldBlockCondition.MAP_CODEC, TestWorldBlockCondition.STREAM_CODEC);

	public static final BlockCondition.Type<BlockStatePropertyBlockCondition> BLOCK_STATE_PROPERTY = registerInternal("block_state_property", BlockStatePropertyBlockCondition.MAP_CODEC, BlockStatePropertyBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<FluidBlockCondition> FLUID = registerInternal("fluid", FluidBlockCondition.MAP_CODEC, FluidBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<HasBlockEntityBlockCondition> HAS_BLOCK_ENTITY = registerInternal("has_block_entity", HasBlockEntityBlockCondition.MAP_CODEC, HasBlockEntityBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<IsInTagBlockCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagBlockCondition.MAP_CODEC, IsInTagBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<IsOfBlockCondition> IS_OF = registerInternal("is_of", IsOfBlockCondition.MAP_CODEC, IsOfBlockCondition.STREAM_CODEC);
	public static final BlockCondition.Type<IsReplaceableBlockCondition> IS_REPLACEABLE = registerInternal("is_replaceable", IsReplaceableBlockCondition.MAP_CODEC, IsReplaceableBlockCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BlockCondition> BlockCondition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BlockCondition> BlockCondition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BLOCK_CONDITION_TYPE, id, new BlockCondition.Type<>(mapCodec, streamCodec));
	}

}
