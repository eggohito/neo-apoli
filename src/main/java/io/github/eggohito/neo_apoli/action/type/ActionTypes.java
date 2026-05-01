package io.github.eggohito.neo_apoli.action.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.*;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.action.type.bientity.BiEntityActionTypes;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.action.type.item.ItemActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ActionTypes {

	public static final ActionType<ConditionalAction> CONDITIONAL = registerInternal("conditional", ConditionalAction.MAP_CODEC, ConditionalAction.STREAM_CODEC);
	public static final ActionType<ExecuteOnEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityAction.MAP_CODEC, ExecuteOnEntityAction.STREAM_CODEC);
	public static final ActionType<LoopAction> LOOP = registerInternal("loop", LoopAction.MAP_CODEC, LoopAction.STREAM_CODEC);
	public static final ActionType<NothingAction> NOTHING = registerInternal("nothing", NothingAction.MAP_CODEC, NothingAction.STREAM_CODEC);
	public static final ActionType<RandomChanceAction> RANDOM = registerInternal("random", RandomChanceAction.MAP_CODEC, RandomChanceAction.STREAM_CODEC);
	public static final ActionType<ReferenceAction> REFERENCE = registerInternal("reference", ReferenceAction.MAP_CODEC, ReferenceAction.STREAM_CODEC);
	public static final ActionType<SequenceAction> SEQUENCE = registerInternal("sequence", SequenceAction.MAP_CODEC, SequenceAction.STREAM_CODEC);
	public static final ActionType<SwitchAction> SWITCH = registerInternal("switch", SwitchAction.MAP_CODEC, SwitchAction.STREAM_CODEC);
	public static final ActionType<WeightedAction> WEIGHTED = registerInternal("weighted", WeightedAction.MAP_CODEC, WeightedAction.STREAM_CODEC);

	public static void registerAll() {
		BiEntityActionTypes.registerAll();
		BlockActionTypes.registerAll();
		EntityActionTypes.registerAll();
		ItemActionTypes.registerAll();
	}

	private static <A extends Action> ActionType<A> registerInternal(String path, MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <A extends Action> ActionType<A> register(ResourceLocation id, MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) {

		var type = new ActionType<A>() {

			@Override
			public MapCodec<A> mapCodec() {
				return mapCodec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, A> streamCodec() {
				return streamCodec;
			}

			@Override
			public ActionKind<?> kind() {
				return ActionKind.INSTANCE;
			}

		};

		return Registry.register(NeoApoliRegistries.ACTION_TYPE, id, type);

	}

}
