package io.github.eggohito.neo_apoli.condition.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.block.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BlockConditionTypes extends ConditionTypes {

	public static final BlockConditionType<AllOfBlockCondition> ALL_OF = registerMetaInternal("all_of", AllOfBlockCondition.CODEC, AllOfBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<AnyOfBlockCondition> ANY_OF = registerMetaInternal("any_of", AnyOfBlockCondition.CODEC, AnyOfBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<CompareBlockCondition> COMPARE = registerMetaInternal("compare", CompareBlockCondition.CODEC, CompareBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<CompareToRangeBlockCondition> COMPARE_TO_RANGE = registerMetaInternal("compare_to_range", CompareToRangeBlockCondition.CODEC, CompareToRangeBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<ConstantBlockCondition> CONSTANT = registerMetaInternal("constant", ConstantBlockCondition.CODEC, ConstantBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<InvertedBlockCondition> INVERTED = registerMetaInternal("inverted", InvertedBlockCondition.CODEC, InvertedBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<OffsetBlockCondition> OFFSET = registerMetaInternal("offset", OffsetBlockCondition.CODEC, OffsetBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<ReferenceBlockCondition> REFERENCE = registerMetaInternal("reference", ReferenceBlockCondition.CODEC, ReferenceBlockCondition.STREAM_CODEC);

	public static final BlockConditionType<BlockStatePropertyBlockCondition> BLOCK_STATE_PROPERTY = registerInternal("block_state_property", BlockStatePropertyBlockCondition.CODEC, BlockStatePropertyBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<FluidBlockCondition> FLUID = registerInternal("fluid", FluidBlockCondition.CODEC, FluidBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<HasBlockEntityBlockCondition> HAS_BLOCK_ENTITY = registerInternal("has_block_entity", HasBlockEntityBlockCondition.CODEC, HasBlockEntityBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<IsInTagBlockCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagBlockCondition.CODEC, IsInTagBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<IsOfBlockCondition> IS_OF = registerInternal("is_of", IsOfBlockCondition.CODEC, IsOfBlockCondition.STREAM_CODEC);
	public static final BlockConditionType<IsReplaceableBlockCondition> IS_REPLACEABLE = registerInternal("is_replaceable", IsReplaceableBlockCondition.CODEC, IsReplaceableBlockCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BlockCondition> BlockConditionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends BlockCondition> BlockConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BlockCondition> BlockConditionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BLOCK_CONDITION_TYPE, id, new BlockConditionType<>(mapCodec, streamCodec));
	}

	public static <C extends BlockCondition> BlockConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(BlockConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
