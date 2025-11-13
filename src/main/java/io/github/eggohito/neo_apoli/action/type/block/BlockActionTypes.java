package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.block.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BlockActionTypes {

	public static final BlockActionType<ChoiceBlockAction> CHOICE = registerInternal("choice", ChoiceBlockAction.CODEC, ChoiceBlockAction.PACKET_CODEC);
	public static final BlockActionType<ConditionalBlockAction> CONDITIONAL = registerInternal("conditional", ConditionalBlockAction.CODEC, ConditionalBlockAction.PACKET_CODEC);
	public static final BlockActionType<LoopBlockAction> LOOP = registerInternal("loop", LoopBlockAction.CODEC, LoopBlockAction.PACKET_CODEC);
	public static final BlockActionType<NothingBlockAction> NOTHING = registerInternal("nothing", NothingBlockAction.CODEC, NothingBlockAction.PACKET_CODEC);
	public static final BlockActionType<OffsetBlockAction> OFFSET = registerInternal("offset", OffsetBlockAction.CODEC, OffsetBlockAction.PACKET_CODEC);
	public static final BlockActionType<RandomChanceBlockAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBlockAction.CODEC, RandomChanceBlockAction.PACKET_CODEC);
	public static final BlockActionType<ReferenceBlockAction> REFERENCE = registerInternal("reference", ReferenceBlockAction.CODEC, ReferenceBlockAction.PACKET_CODEC);
	public static final BlockActionType<SequenceBlockAction> SEQUENCE = registerInternal("sequence", SequenceBlockAction.CODEC, SequenceBlockAction.PACKET_CODEC);
	public static final BlockActionType<WeightedBlockAction> WEIGHTED = registerInternal("weighted", WeightedBlockAction.CODEC, WeightedBlockAction.PACKET_CODEC);

	public static final BlockActionType<AreaOfEffectBlockAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectBlockAction.CODEC, AreaOfEffectBlockAction.PACKET_CODEC);
	public static final BlockActionType<BoneMealBlockAction> BONE_MEAL = registerInternal("bone_meal", BoneMealBlockAction.CODEC, BoneMealBlockAction.PACKET_CODEC);
	public static final BlockActionType<ExecuteCommandBlockAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandBlockAction.CODEC, ExecuteCommandBlockAction.PACKET_CODEC);
	public static final BlockActionType<ModifyBlockStatePropertyBlockAction> MODIFY_BLOCK_STATE_PROPERTY = registerInternal("modify/block_state/property", ModifyBlockStatePropertyBlockAction.CODEC, ModifyBlockStatePropertyBlockAction.PACKET_CODEC);
	public static final BlockActionType<PlaceBlockAction> PLACE = registerInternal("place", PlaceBlockAction.CODEC, PlaceBlockAction.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends BlockAction> BlockActionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BlockAction> BlockActionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ActionTypes.register(id.withPrefixedPath(BlockActionType.PREFIX), Registry.register(NeoApoliRegistries.BLOCK_ACTION_TYPE, id, new BlockActionType<>(mapCodec, packetCodec)));
	}

}
