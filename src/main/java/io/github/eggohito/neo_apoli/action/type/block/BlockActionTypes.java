package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.block.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BlockActionTypes {

	public static final BlockActionType<ConditionalBlockAction> CONDITIONAL = registerInternal("conditional", ConditionalBlockAction.MAP_CODEC, ConditionalBlockAction.STREAM_CODEC);
	public static final BlockActionType<LoopBlockAction> LOOP = registerInternal("loop", LoopBlockAction.MAP_CODEC, LoopBlockAction.STREAM_CODEC);
	public static final BlockActionType<NothingBlockAction> NOTHING = registerInternal("nothing", NothingBlockAction.MAP_CODEC, NothingBlockAction.STREAM_CODEC);
	public static final BlockActionType<OffsetBlockAction> OFFSET = registerInternal("offset", OffsetBlockAction.MAP_CODEC, OffsetBlockAction.STREAM_CODEC);
	public static final BlockActionType<RandomChanceBlockAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBlockAction.MAP_CODEC, RandomChanceBlockAction.STREAM_CODEC);
	public static final BlockActionType<ReferenceBlockAction> REFERENCE = registerInternal("reference", ReferenceBlockAction.MAP_CODEC, ReferenceBlockAction.STREAM_CODEC);
	public static final BlockActionType<SequenceBlockAction> SEQUENCE = registerInternal("sequence", SequenceBlockAction.MAP_CODEC, SequenceBlockAction.STREAM_CODEC);
	public static final BlockActionType<SpawnParticlesBlockAction> SPAWN_PARTICLES = registerInternal("spawn_particles", SpawnParticlesBlockAction.MAP_CODEC, SpawnParticlesBlockAction.STREAM_CODEC);
	public static final BlockActionType<SwitchBlockAction> SWITCH = registerInternal("switch", SwitchBlockAction.MAP_CODEC, SwitchBlockAction.STREAM_CODEC);
	public static final BlockActionType<WeightedBlockAction> WEIGHTED = registerInternal("weighted", WeightedBlockAction.MAP_CODEC, WeightedBlockAction.STREAM_CODEC);

	public static final BlockActionType<AreaOfEffectBlockAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectBlockAction.MAP_CODEC, AreaOfEffectBlockAction.STREAM_CODEC);
	public static final BlockActionType<BoneMealBlockAction> BONE_MEAL = registerInternal("bone_meal", BoneMealBlockAction.MAP_CODEC, BoneMealBlockAction.STREAM_CODEC);
	public static final BlockActionType<ExecuteCommandBlockAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandBlockAction.MAP_CODEC, ExecuteCommandBlockAction.STREAM_CODEC);
	public static final BlockActionType<ModifyBlockStatePropertyBlockAction> MODIFY_BLOCK_STATE_PROPERTY = registerInternal("modify/block_state/property", ModifyBlockStatePropertyBlockAction.MAP_CODEC, ModifyBlockStatePropertyBlockAction.STREAM_CODEC);
	public static final BlockActionType<PlaceBlockAction> PLACE = registerInternal("place", PlaceBlockAction.MAP_CODEC, PlaceBlockAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BlockAction> BlockActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BlockAction> BlockActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BLOCK_ACTION_TYPE, id, new BlockActionType<>(mapCodec, streamCodec));
	}

}
