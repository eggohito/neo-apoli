package io.github.eggohito.neo_apoli.condition.type.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockStatePropertyBlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.HasBlockEntityBlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.InTagBlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.IsOfBlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BlockConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<BlockConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.BLOCK_CONDITION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, BlockConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BLOCK_CONDITION_TYPE);

	public static final BlockConditionType<AllOfBlockCondition> ALL_OF = registerInternal("all_of", AllOfBlockCondition.CODEC, AllOfBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<AnyOfBlockCondition> ANY_OF = registerInternal("any_of", AnyOfBlockCondition.CODEC, AnyOfBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<CompareBlockCondition> COMPARE = registerInternal("compare", CompareBlockCondition.CODEC, CompareBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<ConstantBlockCondition> CONSTANT = registerInternal("constant", ConstantBlockCondition.CODEC, ConstantBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<InvertedBlockCondition> INVERTED = registerInternal("inverted", InvertedBlockCondition.CODEC, InvertedBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<ReferenceBlockCondition> REFERENCE = registerInternal("reference", ReferenceBlockCondition.CODEC, ReferenceBlockCondition.PACKET_CODEC);

	public static final BlockConditionType<BlockStatePropertyBlockCondition> BLOCK_STATE_PROPERTY = registerInternal("block_state_property", BlockStatePropertyBlockCondition.CODEC, BlockStatePropertyBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<HasBlockEntityBlockCondition> HAS_BLOCK_ENTITY = registerInternal("has_block_entity", HasBlockEntityBlockCondition.CODEC, HasBlockEntityBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<InTagBlockCondition> IN_TAG = registerInternal("in_tag", InTagBlockCondition.CODEC, InTagBlockCondition.PACKET_CODEC);
	public static final BlockConditionType<IsOfBlockCondition> IS_OF = registerInternal("is_of", IsOfBlockCondition.CODEC, IsOfBlockCondition.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("and", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_CONDITION_TYPE, ALL_OF));
		ALIASES.addPathAlias("or", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_CONDITION_TYPE, ANY_OF));

		ALIASES.addPathAlias("block_entity", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_CONDITION_TYPE, HAS_BLOCK_ENTITY));
		ALIASES.addPathAlias("block", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_CONDITION_TYPE, IS_OF));

	}

	private static <C extends BlockCondition> BlockConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BlockCondition> BlockConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.BLOCK_CONDITION_TYPE, id, new BlockConditionType<>(mapCodec, packetCodec));
	}

}
