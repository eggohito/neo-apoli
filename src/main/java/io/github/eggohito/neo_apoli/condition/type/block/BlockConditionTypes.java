package io.github.eggohito.neo_apoli.condition.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockConditionTypes extends ConditionTypes {

	public static final BlockConditionType<AllOfBlockCondition> ALL_OF = registerInternal("all_of", AllOfBlockCondition.CODEC, AllOfBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<AnyOfBlockCondition> ANY_OF = registerInternal("any_of", AnyOfBlockCondition.CODEC, AnyOfBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<CompareBlockCondition> COMPARE = registerInternal("compare", CompareBlockCondition.CODEC, CompareBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<CompareToRangeBlockCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeBlockCondition.CODEC, CompareToRangeBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<ConstantBlockCondition> CONSTANT = registerInternal("constant", ConstantBlockCondition.CODEC, ConstantBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<InvertedBlockCondition> INVERTED = registerInternal("inverted", InvertedBlockCondition.CODEC, InvertedBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<OffsetBlockCondition> OFFSET = registerInternal("offset", OffsetBlockCondition.CODEC, OffsetBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<ReferenceBlockCondition> REFERENCE = registerInternal("reference", ReferenceBlockCondition.CODEC, ReferenceBlockCondition.PACKET_CODEC);

	public static final BlockConditionType<BlockStatePropertyBlockCondition> BLOCK_STATE_PROPERTY = registerInternal("block_state_property", BlockStatePropertyBlockCondition.CODEC, BlockStatePropertyBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<HasBlockEntityBlockCondition> HAS_BLOCK_ENTITY = registerInternal("has_block_entity", HasBlockEntityBlockCondition.CODEC, HasBlockEntityBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<IsInTagBlockCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagBlockCondition.CODEC, IsInTagBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<IsOfBlockCondition> IS_OF = registerInternal("is_of", IsOfBlockCondition.CODEC, IsOfBlockCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends BlockCondition> BlockConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BlockCondition> BlockConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ConditionTypes.register(id.withPrefixedPath(BlockConditionType.PREFIX), Registry.register(NeoApoliRegistries.BLOCK_CONDITION_TYPE, id, new BlockConditionType<>(mapCodec, packetCodec)));
	}

}
