package io.github.eggohito.neo_apoli.registry.action;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.bientity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliBiEntityActionTypes {

	public static final BiEntityAction.Type<ConditionalBiEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalBiEntityAction.MAP_CODEC, ConditionalBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<ExecuteOnEntityBiEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityBiEntityAction.MAP_CODEC, ExecuteOnEntityBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<LoopBiEntityAction> LOOP = registerInternal("loop", LoopBiEntityAction.MAP_CODEC, LoopBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<NothingBiEntityAction> NOTHING = registerInternal("nothing", NothingBiEntityAction.MAP_CODEC, NothingBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<RandomChanceBiEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBiEntityAction.MAP_CODEC, RandomChanceBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<ReferenceBiEntityAction> REFERENCE = registerInternal("reference", ReferenceBiEntityAction.MAP_CODEC, ReferenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<SequenceBiEntityAction> SEQUENCE = registerInternal("sequence", SequenceBiEntityAction.MAP_CODEC, SequenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<SwitchBiEntityAction> SWITCH = registerInternal("switch", SwitchBiEntityAction.MAP_CODEC, SwitchBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<WeightedBiEntityAction> WEIGHTED = registerInternal("weighted", WeightedBiEntityAction.MAP_CODEC, WeightedBiEntityAction.STREAM_CODEC);

	public static final BiEntityAction.Type<DamageBiEntityAction> DAMAGE = registerInternal("damage", DamageBiEntityAction.MAP_CODEC, DamageBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<MountBiEntityAction> MOUNT = registerInternal("mount", MountBiEntityAction.MAP_CODEC, MountBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<SwapBiEntityAction> SWAP = registerInternal("swap", SwapBiEntityAction.MAP_CODEC, SwapBiEntityAction.STREAM_CODEC);
	public static final BiEntityAction.Type<TameBiEntityAction> TAME = registerInternal("tame", TameBiEntityAction.MAP_CODEC, TameBiEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityAction> BiEntityAction.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BiEntityAction> BiEntityAction.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_ACTION_TYPE, id, new BiEntityAction.Type<>(mapCodec, streamCodec));
	}

}
