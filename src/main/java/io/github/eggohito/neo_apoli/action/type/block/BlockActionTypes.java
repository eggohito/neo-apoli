package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.custom.block.*;
import io.github.eggohito.neo_apoli.action.meta.block.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BlockActionTypes {

	public static final BlockActionType<ExecuteCommandBlockAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandBlockAction.CODEC, ExecuteCommandBlockAction.PACKET_CODEC);
	public static final BlockActionType<ExplodeBlockAction>	EXPLODE = registerInternal("explode", ExplodeBlockAction.CODEC, ExplodeBlockAction.PACKET_CODEC);
	public static final BlockActionType<IfElseBlockAction> IF_ELSE = registerInternal("if_else", IfElseBlockAction.CODEC, IfElseBlockAction.PACKET_CODEC);
	public static final BlockActionType<IfElseListBlockAction> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListBlockAction.CODEC, IfElseListBlockAction.PACKET_CODEC);
	public static final BlockActionType<LoopBlockAction> LOOP = registerInternal("loop", LoopBlockAction.CODEC, LoopBlockAction.PACKET_CODEC);
	public static final BlockActionType<NothingBlockAction> NOTHING = registerInternal("nothing", NothingBlockAction.CODEC, NothingBlockAction.PACKET_CODEC);
	public static final BlockActionType<OffsetBlockAction> OFFSET = registerInternal("offset", OffsetBlockAction.CODEC, OffsetBlockAction.PACKET_CODEC);
	public static final BlockActionType<RandomChanceBlockAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBlockAction.CODEC, RandomChanceBlockAction.PACKET_CODEC);
	public static final BlockActionType<RandomChoiceBlockAction> RANDOM_CHOICE = registerInternal("random_choice", RandomChoiceBlockAction.CODEC, RandomChoiceBlockAction.PACKET_CODEC);
	public static final BlockActionType<ReferenceBlockAction> REFERENCE = registerInternal("reference", ReferenceBlockAction.CODEC, ReferenceBlockAction.PACKET_CODEC);
	public static final BlockActionType<SequenceBlockAction> SEQUENCE = registerInternal("sequence", SequenceBlockAction.CODEC, SequenceBlockAction.PACKET_CODEC);

	public static final BlockActionType<AddBlockBlockAction> ADD_BLOCK = registerInternal("add_block", AddBlockBlockAction.CODEC, AddBlockBlockAction.PACKET_CODEC);
	public static final BlockActionType<AreaOfEffectBlockAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectBlockAction.CODEC, AreaOfEffectBlockAction.PACKET_CODEC);
	public static final BlockActionType<BoneMealBlockAction> BONE_MEAL = registerInternal("bone_meal", BoneMealBlockAction.CODEC, BoneMealBlockAction.PACKET_CODEC);
	public static final BlockActionType<ModifyBlockStatePropertyBlockAction> MODIFY_BLOCK_STATE_PROPERTY = registerInternal("modify_block_state_property", ModifyBlockStatePropertyBlockAction.CODEC, ModifyBlockStatePropertyBlockAction.PACKET_CODEC);
	public static final BlockActionType<SetBlockBlockAction> SET_BLOCK = registerInternal("set_block", SetBlockBlockAction.CODEC, SetBlockBlockAction.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <A extends BlockAction> BlockActionType<A> registerInternal(String path, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <A extends BlockAction> BlockActionType<A> register(Identifier id, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return Registry.register(NeoApoliRegistries.BLOCK_ACTION_TYPE, id, new BlockActionType<>(mapCodec, packetCodec));
	}

}
