package io.github.eggohito.neo_apoli.action.type.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.custom.bientity.*;
import io.github.eggohito.neo_apoli.action.meta.bientity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BiEntityActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<BiEntityActionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.BIENTITY_ACTION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, BiEntityActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BIENTITY_ACTION_TYPE);

	public static final BiEntityActionType<ExecuteCommandBiEntityAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandBiEntityAction.CODEC, ExecuteCommandBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<IfElseBiEntityAction> IF_ELSE = registerInternal("if_else", IfElseBiEntityAction.CODEC, IfElseBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<IfElseListBiEntityAction> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListBiEntityAction.CODEC, IfElseListBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<LoopBiEntityAction> LOOP = registerInternal("loop", LoopBiEntityAction.CODEC, LoopBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<NothingBiEntityAction> NOTHING = registerInternal("nothing", NothingBiEntityAction.CODEC, NothingBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<RandomChanceBiEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceBiEntityAction.CODEC, RandomChanceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<RandomChoiceBiEntityAction> RANDOM_CHOICE = registerInternal("random_choice", RandomChoiceBiEntityAction.CODEC, RandomChoiceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<ReferenceBiEntityAction> REFERENCE = registerInternal("reference", ReferenceBiEntityAction.CODEC, ReferenceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<SequenceBiEntityAction> SEQUENCE = registerInternal("sequence", SequenceBiEntityAction.CODEC, SequenceBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<SideBiEntityAction> SIDE = registerInternal("side", SideBiEntityAction.CODEC, SideBiEntityAction.PACKET_CODEC);

	public static final BiEntityActionType<DamageBiEntityAction> DAMAGE = registerInternal("damage", DamageBiEntityAction.CODEC, DamageBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<ExecuteEntityActionBiEntityAction> EXECUTE_ENTITY_ACTION = registerInternal("execute_entity_action", ExecuteEntityActionBiEntityAction.CODEC, ExecuteEntityActionBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<MountBiEntityAction> MOUNT = registerInternal("mount", MountBiEntityAction.CODEC, MountBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<SwapEntityContextBiEntityAction> SWAP_ENTITY_CONTEXT = registerInternal("swap_entity_context", SwapEntityContextBiEntityAction.CODEC, SwapEntityContextBiEntityAction.PACKET_CODEC);
	public static final BiEntityActionType<TameBiEntityAction> TAME = registerInternal("tame", TameBiEntityAction.CODEC, TameBiEntityAction.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("no_op", getId(NOTHING).getPath());
		ALIASES.addPathAlias("chance", getId(RANDOM_CHANCE).getPath());
		ALIASES.addPathAlias("choice", getId(RANDOM_CHOICE).getPath());
		ALIASES.addPathAlias("and", getId(SEQUENCE).getPath());

		ALIASES.addPathAlias("start_riding", getId(MOUNT).getPath());

	}

	private static <A extends BiEntityAction> BiEntityActionType<A> registerInternal(String path, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <A extends BiEntityAction> BiEntityActionType<A> register(Identifier id, MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_ACTION_TYPE, id, new BiEntityActionType<>(mapCodec, packetCodec));
	}

	public static Identifier getId(BiEntityActionType<?> type) {
		return RegistryUtil.getId(NeoApoliRegistries.BIENTITY_ACTION_TYPE, type);
	}

}
