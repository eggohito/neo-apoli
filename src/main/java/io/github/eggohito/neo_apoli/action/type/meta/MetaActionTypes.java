package io.github.eggohito.neo_apoli.action.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.meta.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class MetaActionTypes {

	public static final MetaActionType<ChoiceMetaAction> CHOICE = registerInternal("choice", ChoiceMetaAction.MAP_CODEC, ChoiceMetaAction.STREAM_CODEC);
	public static final MetaActionType<ConditionalMetaAction> CONDITIONAL = registerInternal("conditional", ConditionalMetaAction.MAP_CODEC, ConditionalMetaAction.STREAM_CODEC);
	public static final MetaActionType<ExecuteOnEntityMetaAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityMetaAction.MAP_CODEC, ExecuteOnEntityMetaAction.STREAM_CODEC);
	public static final MetaActionType<LoopMetaAction> LOOP = registerInternal("loop", LoopMetaAction.MAP_CODEC, LoopMetaAction.STREAM_CODEC);
	public static final MetaActionType<NothingMetaAction> NOTHING = registerInternal("nothing", NothingMetaAction.MAP_CODEC, NothingMetaAction.STREAM_CODEC);
	public static final MetaActionType<RandomChanceMetaAction> RANDOM = registerInternal("random", RandomChanceMetaAction.MAP_CODEC, RandomChanceMetaAction.STREAM_CODEC);
	public static final MetaActionType<ReferenceMetaAction> REFERENCE = registerInternal("reference", ReferenceMetaAction.MAP_CODEC, ReferenceMetaAction.STREAM_CODEC);
	public static final MetaActionType<SequenceMetaAction> SEQUENCE = registerInternal("sequence", SequenceMetaAction.MAP_CODEC, SequenceMetaAction.STREAM_CODEC);
	public static final MetaActionType<WeightedMetaAction> WEIGHTED = registerInternal("weighted", WeightedMetaAction.MAP_CODEC, WeightedMetaAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends MetaAction> MetaActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends MetaAction> MetaActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return ActionTypes.register(id, new MetaActionType<>(mapCodec, packetCodec));
	}

}
