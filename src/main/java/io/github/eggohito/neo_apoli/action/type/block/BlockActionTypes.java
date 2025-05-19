package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.custom.block.AddBlockBlockAction;
import io.github.eggohito.neo_apoli.action.custom.block.ModifyBlockStatePropertyBlockAction;
import io.github.eggohito.neo_apoli.action.custom.block.SetBlockBlockAction;
import io.github.eggohito.neo_apoli.action.meta.block.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BlockActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<BlockActionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.BLOCK_ACTION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, BlockActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BLOCK_ACTION_TYPE);

	public static final BlockActionType<ExecuteCommandBlockAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandBlockAction.CODEC, ExecuteCommandBlockAction.PACKET_CODEC);
	public static final BlockActionType<IfElseListBlockAction> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListBlockAction.CODEC, IfElseListBlockAction.PACKET_CODEC);
	public static final BlockActionType<NothingBlockAction> NOTHING = registerInternal("nothing", NothingBlockAction.CODEC, NothingBlockAction.PACKET_CODEC);
	public static final BlockActionType<OffsetBlockAction> OFFSET = registerInternal("offset", OffsetBlockAction.CODEC, OffsetBlockAction.PACKET_CODEC);
	public static final BlockActionType<RandomChanceBlockAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBlockAction.CODEC, RandomChanceBlockAction.PACKET_CODEC);
	public static final BlockActionType<RandomChoiceBlockAction> RANDOM_CHOICE = registerInternal("random_choice", RandomChoiceBlockAction.CODEC, RandomChoiceBlockAction.PACKET_CODEC);
	public static final BlockActionType<ReferenceBlockAction> REFERENCE = registerInternal("reference", ReferenceBlockAction.CODEC, ReferenceBlockAction.PACKET_CODEC);
	public static final BlockActionType<SequenceBlockAction> SEQUENCE = registerInternal("sequence", SequenceBlockAction.CODEC, SequenceBlockAction.PACKET_CODEC);
	public static final BlockActionType<SideBlockAction> SIDE = registerInternal("side", SideBlockAction.CODEC, SideBlockAction.PACKET_CODEC);

	public static final BlockActionType<AddBlockBlockAction> ADD_BLOCK = registerInternal("add_block", AddBlockBlockAction.CODEC, AddBlockBlockAction.PACKET_CODEC);
	public static final BlockActionType<ModifyBlockStatePropertyBlockAction> MODIFY_BLOCK_STATE_PROPERTY = registerInternal("modify_block_state_property", ModifyBlockStatePropertyBlockAction.CODEC, ModifyBlockStatePropertyBlockAction.PACKET_CODEC);
	public static final BlockActionType<SetBlockBlockAction> SET_BLOCK = registerInternal("set_block", SetBlockBlockAction.CODEC, SetBlockBlockAction.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("no_op", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_ACTION_TYPE, NOTHING));
		ALIASES.addPathAlias("chance", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_ACTION_TYPE, RANDOM_CHANCE));
		ALIASES.addPathAlias("choice", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_ACTION_TYPE, RANDOM_CHOICE));
		ALIASES.addPathAlias("and", RegistryUtil.getIdPath(NeoApoliRegistries.BLOCK_ACTION_TYPE, SEQUENCE));

	}

	private static <A extends BlockAction> BlockActionType<A> registerInternal(String path, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <A extends BlockAction> BlockActionType<A> register(Identifier id, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return Registry.register(NeoApoliRegistries.BLOCK_ACTION_TYPE, id, new BlockActionType<>(mapCodec, packetCodec));
	}

}
