package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.block.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BlockActionTypes {

	public static final BlockActionType<ChoiceBlockAction> CHOICE = registerInternal("choice", ChoiceBlockAction.CODEC, ChoiceBlockAction.STREAM_CODEC);
	public static final BlockActionType<ConditionalBlockAction> CONDITIONAL = registerInternal("conditional", ConditionalBlockAction.CODEC, ConditionalBlockAction.STREAM_CODEC);
	public static final BlockActionType<LoopBlockAction> LOOP = registerInternal("loop", LoopBlockAction.CODEC, LoopBlockAction.STREAM_CODEC);
	public static final BlockActionType<NothingBlockAction> NOTHING = registerInternal("nothing", NothingBlockAction.CODEC, NothingBlockAction.STREAM_CODEC);
	public static final BlockActionType<OffsetBlockAction> OFFSET = registerInternal("offset", OffsetBlockAction.CODEC, OffsetBlockAction.STREAM_CODEC);
	public static final BlockActionType<RandomChanceBlockAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBlockAction.CODEC, RandomChanceBlockAction.STREAM_CODEC);
	public static final BlockActionType<ReferenceBlockAction> REFERENCE = registerInternal("reference", ReferenceBlockAction.CODEC, ReferenceBlockAction.STREAM_CODEC);
	public static final BlockActionType<SequenceBlockAction> SEQUENCE = registerInternal("sequence", SequenceBlockAction.CODEC, SequenceBlockAction.STREAM_CODEC);
	public static final BlockActionType<WeightedBlockAction> WEIGHTED = registerInternal("weighted", WeightedBlockAction.CODEC, WeightedBlockAction.STREAM_CODEC);

	public static final BlockActionType<AreaOfEffectBlockAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectBlockAction.CODEC, AreaOfEffectBlockAction.STREAM_CODEC);
	public static final BlockActionType<BoneMealBlockAction> BONE_MEAL = registerInternal("bone_meal", BoneMealBlockAction.CODEC, BoneMealBlockAction.STREAM_CODEC);
	public static final BlockActionType<ExecuteCommandBlockAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandBlockAction.CODEC, ExecuteCommandBlockAction.STREAM_CODEC);
	public static final BlockActionType<ModifyBlockStatePropertyBlockAction> MODIFY_BLOCK_STATE_PROPERTY = registerInternal("modify/block_state/property", ModifyBlockStatePropertyBlockAction.CODEC, ModifyBlockStatePropertyBlockAction.STREAM_CODEC);
	public static final BlockActionType<PlaceBlockAction> PLACE = registerInternal("place", PlaceBlockAction.CODEC, PlaceBlockAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BlockAction> BlockActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BlockAction> BlockActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(BlockActionType.PREFIX);
		return ActionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.BLOCK_ACTION_TYPE, prefixedId, new BlockActionType<>(mapCodec, streamCodec)));
	}

}
