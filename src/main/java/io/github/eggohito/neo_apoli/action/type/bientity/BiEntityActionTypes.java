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

	public static final BiEntityActionType<ChoiceBiEntityAction> CHOICE = registerMetaInternal("choice", ChoiceBiEntityAction.CODEC, ChoiceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ConditionalBiEntityAction> CONDITIONAL = registerMetaInternal("conditional", ConditionalBiEntityAction.CODEC, ConditionalBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ExecuteOnEntityBiEntityAction> EXECUTE_ON_ENTITY = registerMetaInternal("execute_on_entity", ExecuteOnEntityBiEntityAction.CODEC, ExecuteOnEntityBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<LoopBiEntityAction> LOOP = registerMetaInternal("loop", LoopBiEntityAction.CODEC, LoopBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<NothingBiEntityAction> NOTHING = registerMetaInternal("nothing", NothingBiEntityAction.CODEC, NothingBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<RandomChanceBiEntityAction> RANDOM_CHANCE = registerMetaInternal("random_chance", RandomChanceBiEntityAction.CODEC, RandomChanceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<ReferenceBiEntityAction> REFERENCE = registerMetaInternal("reference", ReferenceBiEntityAction.CODEC, ReferenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SequenceBiEntityAction> SEQUENCE = registerMetaInternal("sequence", SequenceBiEntityAction.CODEC, SequenceBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<WeightedBiEntityAction> WEIGHTED = registerMetaInternal("weighted", WeightedBiEntityAction.CODEC, WeightedBiEntityAction.STREAM_CODEC);

	public static final BiEntityActionType<DamageBiEntityAction> DAMAGE = registerInternal("damage", DamageBiEntityAction.CODEC, DamageBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<MountBiEntityAction> MOUNT = registerInternal("mount", MountBiEntityAction.CODEC, MountBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<SwapBiEntityAction> SWAP = registerInternal("swap", SwapBiEntityAction.CODEC, SwapBiEntityAction.STREAM_CODEC);
	public static final BiEntityActionType<TameBiEntityAction> TAME = registerInternal("tame", TameBiEntityAction.CODEC, TameBiEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityAction> BiEntityActionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends BiEntityAction> BiEntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends BiEntityAction> BiEntityActionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_ACTION_TYPE, id, new BiEntityActionType<>(mapCodec, streamCodec));
	}

	public static <C extends BiEntityAction> BiEntityActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(BiEntityActionType.PREFIX);
		return ActionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
