package io.github.eggohito.neo_apoli.action.type.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.bientity.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class BiEntityActionTypes {

	public static final BiEntityActionType<ChoiceBiEntityAction> CHOICE = registerInternal("choice", ChoiceBiEntityAction.CODEC, ChoiceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<ConditionalBiEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalBiEntityAction.CODEC, ConditionalBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<ExecuteOnEntityBiEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityBiEntityAction.CODEC, ExecuteOnEntityBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<LoopBiEntityAction> LOOP = registerInternal("loop", LoopBiEntityAction.CODEC, LoopBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<NothingBiEntityAction> NOTHING = registerInternal("nothing", NothingBiEntityAction.CODEC, NothingBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<RandomChanceBiEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBiEntityAction.CODEC, RandomChanceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<ReferenceBiEntityAction> REFERENCE = registerInternal("reference", ReferenceBiEntityAction.CODEC, ReferenceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<SequenceBiEntityAction> SEQUENCE = registerInternal("sequence", SequenceBiEntityAction.CODEC, SequenceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<WeightedBiEntityAction> WEIGHTED = registerInternal("weighted", WeightedBiEntityAction.CODEC, WeightedBiEntityAction.PACKET_CODEC);

	public static final BiEntityActionType<DamageBiEntityAction> DAMAGE = registerInternal("damage", DamageBiEntityAction.CODEC, DamageBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<MountBiEntityAction> MOUNT = registerInternal("mount", MountBiEntityAction.CODEC, MountBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<SwapBiEntityAction> SWAP = registerInternal("swap", SwapBiEntityAction.CODEC, SwapBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<TameBiEntityAction> TAME = registerInternal("tame", TameBiEntityAction.CODEC, TameBiEntityAction.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityAction> BiEntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BiEntityAction> BiEntityActionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ActionTypes.register(id.withPrefixedPath(BiEntityActionType.PREFIX), Registry.register(NeoApoliRegistries.BIENTITY_ACTION_TYPE, id, new BiEntityActionType<>(mapCodec, packetCodec)));
	}

}
