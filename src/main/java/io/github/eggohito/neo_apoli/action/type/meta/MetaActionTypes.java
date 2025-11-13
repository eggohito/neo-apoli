package io.github.eggohito.neo_apoli.action.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.*;
import io.github.eggohito.neo_apoli.action.custom.meta.MetaAction;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public class MetaActionTypes {

	public static final MetaActionType<ChoiceAction> CHOICE = registerInternal("choice", ChoiceAction.CODEC, ChoiceAction.PACKET_CODEC);
	public static final MetaActionType<ConditionalAction> CONDITIONAL = registerInternal("conditional", ConditionalAction.CODEC, ConditionalAction.PACKET_CODEC);
	public static final MetaActionType<ExecuteOnEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityAction.CODEC, ExecuteOnEntityAction.PACKET_CODEC);
	public static final MetaActionType<LoopAction> LOOP = registerInternal("loop", LoopAction.CODEC, LoopAction.PACKET_CODEC);
	public static final MetaActionType<NothingAction> NOTHING = registerInternal("nothing", NothingAction.CODEC, NothingAction.PACKET_CODEC);
	public static final MetaActionType<RandomChanceAction> RANDOM = registerInternal("random", RandomChanceAction.CODEC, RandomChanceAction.PACKET_CODEC);
	public static final MetaActionType<ReferenceAction> REFERENCE = registerInternal("reference", ReferenceAction.CODEC, ReferenceAction.PACKET_CODEC);
	public static final MetaActionType<SequenceAction> SEQUENCE = registerInternal("sequence", SequenceAction.CODEC, SequenceAction.PACKET_CODEC);
	public static final MetaActionType<WeightedAction> WEIGHTED = registerInternal("weighted", WeightedAction.CODEC, WeightedAction.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends MetaAction> MetaActionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends MetaAction> MetaActionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ActionTypes.register(id, new MetaActionType<>(mapCodec, packetCodec));
	}

}
