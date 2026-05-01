package io.github.eggohito.neo_apoli.action.type.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.bientity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BiEntityActionTypes {

	public static final BiEntityActionType<ConditionalBiEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalBiEntityAction.MAP_CODEC, ConditionalBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ExecuteOnEntityBiEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityBiEntityAction.MAP_CODEC, ExecuteOnEntityBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<LoopBiEntityAction> LOOP = registerInternal("loop", LoopBiEntityAction.MAP_CODEC, LoopBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<NothingBiEntityAction> NOTHING = registerInternal("nothing", NothingBiEntityAction.MAP_CODEC, NothingBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<RandomChanceBiEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBiEntityAction.MAP_CODEC, RandomChanceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ReferenceBiEntityAction> REFERENCE = registerInternal("reference", ReferenceBiEntityAction.MAP_CODEC, ReferenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SequenceBiEntityAction> SEQUENCE = registerInternal("sequence", SequenceBiEntityAction.MAP_CODEC, SequenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SwitchBiEntityAction> SWITCH = registerInternal("switch", SwitchBiEntityAction.MAP_CODEC, SwitchBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<WeightedBiEntityAction> WEIGHTED = registerInternal("weighted", WeightedBiEntityAction.MAP_CODEC, WeightedBiEntityAction.STREAM_CODEC);

	public static final BiEntityActionType<DamageBiEntityAction> DAMAGE = registerInternal("damage", DamageBiEntityAction.MAP_CODEC, DamageBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<MountBiEntityAction> MOUNT = registerInternal("mount", MountBiEntityAction.MAP_CODEC, MountBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SwapBiEntityAction> SWAP = registerInternal("swap", SwapBiEntityAction.MAP_CODEC, SwapBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<TameBiEntityAction> TAME = registerInternal("tame", TameBiEntityAction.MAP_CODEC, TameBiEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityAction> BiEntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BiEntityAction> BiEntityActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_ACTION_TYPE, id, new BiEntityActionType<>(mapCodec, streamCodec));
	}

}
