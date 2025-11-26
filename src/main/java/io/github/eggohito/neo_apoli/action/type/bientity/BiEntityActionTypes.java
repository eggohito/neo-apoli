package io.github.eggohito.neo_apoli.action.type.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.bientity.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BiEntityActionTypes {

	public static final BiEntityActionType<ChoiceBiEntityAction> CHOICE = registerInternal("choice", ChoiceBiEntityAction.CODEC, ChoiceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ConditionalBiEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalBiEntityAction.CODEC, ConditionalBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ExecuteOnEntityBiEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityBiEntityAction.CODEC, ExecuteOnEntityBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<LoopBiEntityAction> LOOP = registerInternal("loop", LoopBiEntityAction.CODEC, LoopBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<NothingBiEntityAction> NOTHING = registerInternal("nothing", NothingBiEntityAction.CODEC, NothingBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<RandomChanceBiEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBiEntityAction.CODEC, RandomChanceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ReferenceBiEntityAction> REFERENCE = registerInternal("reference", ReferenceBiEntityAction.CODEC, ReferenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SequenceBiEntityAction> SEQUENCE = registerInternal("sequence", SequenceBiEntityAction.CODEC, SequenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<WeightedBiEntityAction> WEIGHTED = registerInternal("weighted", WeightedBiEntityAction.CODEC, WeightedBiEntityAction.STREAM_CODEC);

	public static final BiEntityActionType<DamageBiEntityAction> DAMAGE = registerInternal("damage", DamageBiEntityAction.CODEC, DamageBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<MountBiEntityAction> MOUNT = registerInternal("mount", MountBiEntityAction.CODEC, MountBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SwapBiEntityAction> SWAP = registerInternal("swap", SwapBiEntityAction.CODEC, SwapBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<TameBiEntityAction> TAME = registerInternal("tame", TameBiEntityAction.CODEC, TameBiEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityAction> BiEntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BiEntityAction> BiEntityActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return ActionTypes.register(id.withPrefix(BiEntityActionType.PREFIX), Registry.register(NeoApoliRegistries.BIENTITY_ACTION_TYPE, id, new BiEntityActionType<>(mapCodec, packetCodec)));
	}

}
