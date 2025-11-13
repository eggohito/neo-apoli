package io.github.eggohito.neo_apoli.action.type.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.entity.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityActionTypes {

	public static final EntityActionType<ChoiceEntityAction> CHOICE = registerInternal("choice", ChoiceEntityAction.CODEC, ChoiceEntityAction.PACKET_CODEC);
	public static final EntityActionType<ConditionalEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalEntityAction.CODEC, ConditionalEntityAction.PACKET_CODEC);
	public static final EntityActionType<ExecuteOnEntityEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityEntityAction.CODEC, ExecuteOnEntityEntityAction.PACKET_CODEC);
	public static final EntityActionType<LoopEntityAction> LOOP = registerInternal("loop", LoopEntityAction.CODEC, LoopEntityAction.PACKET_CODEC);
	public static final EntityActionType<NothingEntityAction> NOTHING = registerInternal("nothing", NothingEntityAction.CODEC, NothingEntityAction.PACKET_CODEC);
	public static final EntityActionType<OffsetEntityAction> OFFSET = registerInternal("offset", OffsetEntityAction.CODEC, OffsetEntityAction.PACKET_CODEC);
	public static final EntityActionType<RandomChanceEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceEntityAction.CODEC, RandomChanceEntityAction.PACKET_CODEC);
	public static final EntityActionType<ReferenceEntityAction> REFERENCE = registerInternal("reference", ReferenceEntityAction.CODEC, ReferenceEntityAction.PACKET_CODEC);
	public static final EntityActionType<SequenceEntityAction> SEQUENCE = registerInternal("sequence", SequenceEntityAction.CODEC, SequenceEntityAction.PACKET_CODEC);
	public static final EntityActionType<WeightedEntityAction> WEIGHTED = registerInternal("weighted", WeightedEntityAction.CODEC, WeightedEntityAction.PACKET_CODEC);

	public static final EntityActionType<AddExperienceEntityAction> ADD_EXPERIENCE = registerInternal("add_experience", AddExperienceEntityAction.CODEC, AddExperienceEntityAction.PACKET_CODEC);
	public static final EntityActionType<AreaOfEffectEntityAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectEntityAction.CODEC, AreaOfEffectEntityAction.PACKET_CODEC);
	public static final EntityActionType<DamageEntityAction> DAMAGE = registerInternal("damage", DamageEntityAction.CODEC, DamageEntityAction.PACKET_CODEC);
	public static final EntityActionType<DismountEntityAction> DISMOUNT = registerInternal("dismount", DismountEntityAction.CODEC, DismountEntityAction.PACKET_CODEC);
	public static final EntityActionType<EmitGameEventEntityAction> EMIT_GAME_EVENT = registerInternal("emit_game_event", EmitGameEventEntityAction.CODEC, EmitGameEventEntityAction.PACKET_CODEC);
	public static final EntityActionType<ExecuteCommandEntityAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandEntityAction.CODEC, ExecuteCommandEntityAction.PACKET_CODEC);
	public static final EntityActionType<ExtinguishEntityAction> EXTINGUISH = registerInternal("extinguish", ExtinguishEntityAction.CODEC, ExtinguishEntityAction.PACKET_CODEC);
	public static final EntityActionType<GiveItemsEntityAction> GIVE_ITEMS = registerInternal("give_items", GiveItemsEntityAction.CODEC, GiveItemsEntityAction.PACKET_CODEC);
	public static final EntityActionType<SetOnFireEntityAction> SET_ON_FIRE = registerInternal("set_on_fire", SetOnFireEntityAction.CODEC, SetOnFireEntityAction.PACKET_CODEC);
	public static final EntityActionType<SwingHandEntityAction> SWING_HAND = registerInternal("swing_hand", SwingHandEntityAction.CODEC, SwingHandEntityAction.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityAction> EntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends EntityAction> EntityActionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ActionTypes.register(id.withPrefixedPath(EntityActionType.PREFIX), Registry.register(NeoApoliRegistries.ENTITY_ACTION_TYPE, id, new EntityActionType<>(mapCodec, packetCodec)));
	}

}
