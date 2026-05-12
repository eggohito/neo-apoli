package io.github.eggohito.neo_apoli.registry.action;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliActionTypes {

	public static final Action.Type<ConditionalAction> CONDITIONAL = registerInternal("conditional", ConditionalAction.MAP_CODEC, ConditionalAction.STREAM_CODEC);
	public static final Action.Type<ExecuteOnEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityAction.MAP_CODEC, ExecuteOnEntityAction.STREAM_CODEC);
	public static final Action.Type<LoopAction> LOOP = registerInternal("loop", LoopAction.MAP_CODEC, LoopAction.STREAM_CODEC);
	public static final Action.Type<NothingAction> NOTHING = registerInternal("nothing", NothingAction.MAP_CODEC, NothingAction.STREAM_CODEC);
	public static final Action.Type<RandomChanceAction> RANDOM = registerInternal("random", RandomChanceAction.MAP_CODEC, RandomChanceAction.STREAM_CODEC);
	public static final Action.Type<ReferenceAction> REFERENCE = registerInternal("reference", ReferenceAction.MAP_CODEC, ReferenceAction.STREAM_CODEC);
	public static final Action.Type<SequenceAction> SEQUENCE = registerInternal("sequence", SequenceAction.MAP_CODEC, SequenceAction.STREAM_CODEC);
	public static final Action.Type<SwitchAction> SWITCH = registerInternal("switch", SwitchAction.MAP_CODEC, SwitchAction.STREAM_CODEC);
	public static final Action.Type<WeightedAction> WEIGHTED = registerInternal("weighted", WeightedAction.MAP_CODEC, WeightedAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <A extends Action> Action.Type<A> registerInternal(String path, MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <A extends Action> Action.Type<A> register(ResourceLocation id, MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) {

		var type = new Action.Type<A>() {

			@Override
			public MapCodec<A> mapCodec() {
				return mapCodec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, A> streamCodec() {
				return streamCodec;
			}

			@Override
			public Action.Kind<?> kind() {
				return Action.Kind.INSTANCE;
			}

		};

		return Registry.register(NeoApoliRegistries.ACTION_TYPE, id, type);

	}

}
