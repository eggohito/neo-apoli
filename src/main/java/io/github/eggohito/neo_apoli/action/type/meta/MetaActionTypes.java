package io.github.eggohito.neo_apoli.action.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.*;
import io.github.eggohito.neo_apoli.action.custom.meta.MetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class MetaActionTypes {

	public static final MetaActionType<ChoiceAction> CHOICE = registerInternal("choice", ChoiceAction.CODEC, ChoiceAction.STREAM_CODEC);
	public static final MetaActionType<ConditionalAction> CONDITIONAL = registerInternal("conditional", ConditionalAction.CODEC, ConditionalAction.STREAM_CODEC);
	public static final MetaActionType<ExecuteOnEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityAction.CODEC, ExecuteOnEntityAction.STREAM_CODEC);
	public static final MetaActionType<LoopAction> LOOP = registerInternal("loop", LoopAction.CODEC, LoopAction.STREAM_CODEC);
	public static final MetaActionType<NothingAction> NOTHING = registerInternal("nothing", NothingAction.CODEC, NothingAction.STREAM_CODEC);
	public static final MetaActionType<RandomChanceAction> RANDOM = registerInternal("random", RandomChanceAction.CODEC, RandomChanceAction.STREAM_CODEC);
	public static final MetaActionType<ReferenceAction> REFERENCE = registerInternal("reference", ReferenceAction.CODEC, ReferenceAction.STREAM_CODEC);
	public static final MetaActionType<SequenceAction> SEQUENCE = registerInternal("sequence", SequenceAction.CODEC, SequenceAction.STREAM_CODEC);
	public static final MetaActionType<WeightedAction> WEIGHTED = registerInternal("weighted", WeightedAction.CODEC, WeightedAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends MetaAction> MetaActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends MetaAction> MetaActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return ActionTypes.register(id, new MetaActionType<>(mapCodec, packetCodec));
	}

}
