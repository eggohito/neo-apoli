package io.github.eggohito.neo_apoli.action.type.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.custom.entity.*;
import io.github.eggohito.neo_apoli.action.meta.entity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class EntityActionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<EntityActionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.ENTITY_ACTION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, EntityActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ENTITY_ACTION_TYPE);

	public static final EntityActionType<ExecuteCommandEntityAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandEntityAction.CODEC, ExecuteCommandEntityAction.PACKET_CODEC);
	public static final EntityActionType<IfElseEntityAction> IF_ELSE = registerInternal("if_else", IfElseEntityAction.CODEC, IfElseEntityAction.PACKET_CODEC);
	public static final EntityActionType<IfElseListEntityAction> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListEntityAction.CODEC, IfElseListEntityAction.PACKET_CODEC);
	public static final EntityActionType<NothingEntityAction> NOTHING = registerInternal("nothing", NothingEntityAction.CODEC, NothingEntityAction.PACKET_CODEC);
	public static final EntityActionType<OffsetEntityAction> OFFSET = registerInternal("offset", OffsetEntityAction.CODEC, OffsetEntityAction.PACKET_CODEC);
	public static final EntityActionType<RandomChanceEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceEntityAction.CODEC, RandomChanceEntityAction.PACKET_CODEC);
	public static final EntityActionType<RandomChoiceEntityAction> RANDOM_CHOICE = registerInternal("random_choice", RandomChoiceEntityAction.CODEC, RandomChoiceEntityAction.PACKET_CODEC);
	public static final EntityActionType<ReferenceEntityAction> REFERENCE = registerInternal("reference", ReferenceEntityAction.CODEC, ReferenceEntityAction.PACKET_CODEC);
	public static final EntityActionType<SequenceEntityAction> SEQUENCE = registerInternal("sequence", SequenceEntityAction.CODEC, SequenceEntityAction.PACKET_CODEC);
	public static final EntityActionType<SideEntityAction> SIDE = registerInternal("side", SideEntityAction.CODEC, SideEntityAction.PACKET_CODEC);

	public static final EntityActionType<AddExperienceEntityAction> ADD_EXPERIENCE = registerInternal("add_experience", AddExperienceEntityAction.CODEC, AddExperienceEntityAction.PACKET_CODEC);
	public static final EntityActionType<DamageEntityAction> DAMAGE = registerInternal("damage", DamageEntityAction.CODEC, DamageEntityAction.PACKET_CODEC);
	public static final EntityActionType<EmitGameEventEntityAction> EMIT_GAME_EVENT = registerInternal("emit_game_event", EmitGameEventEntityAction.CODEC, EmitGameEventEntityAction.PACKET_CODEC);
	public static final EntityActionType<ExtinguishEntityAction> EXTINGUISH = registerInternal("extinguish", ExtinguishEntityAction.CODEC, ExtinguishEntityAction.PACKET_CODEC);
	public static final EntityActionType<SetOnFireEntityAction> SET_ON_FIRE = registerInternal("set_on_fire", SetOnFireEntityAction.CODEC, SetOnFireEntityAction.PACKET_CODEC);
	public static final EntityActionType<SwingHandEntityAction> SWING_HAND = registerInternal("swing_hand", SwingHandEntityAction.CODEC, SwingHandEntityAction.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("no_op", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_ACTION_TYPE, NOTHING));
		ALIASES.addPathAlias("chance", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_ACTION_TYPE, RANDOM_CHANCE));
		ALIASES.addPathAlias("choice", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_ACTION_TYPE, RANDOM_CHOICE));
		ALIASES.addPathAlias("and", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_ACTION_TYPE, SEQUENCE));

		ALIASES.addPathAlias("add_xp", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_ACTION_TYPE, ADD_EXPERIENCE));

	}

	private static <C extends EntityAction> EntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends EntityAction> EntityActionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_ACTION_TYPE, id, new EntityActionType<>(mapCodec, packetCodec));
	}

}
