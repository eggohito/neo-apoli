package io.github.eggohito.neo_apoli.action.type.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.entity.*;
import io.github.eggohito.neo_apoli.action.type.ActionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class EntityActionTypes {

	public static final EntityActionType<ChoiceEntityAction> CHOICE = registerInternal("choice", ChoiceEntityAction.CODEC, ChoiceEntityAction.STREAM_CODEC);
	public static final EntityActionType<ConditionalEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalEntityAction.CODEC, ConditionalEntityAction.STREAM_CODEC);
	public static final EntityActionType<ExecuteOnEntityEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityEntityAction.CODEC, ExecuteOnEntityEntityAction.STREAM_CODEC);
	public static final EntityActionType<LoopEntityAction> LOOP = registerInternal("loop", LoopEntityAction.CODEC, LoopEntityAction.STREAM_CODEC);
	public static final EntityActionType<NothingEntityAction> NOTHING = registerInternal("nothing", NothingEntityAction.CODEC, NothingEntityAction.STREAM_CODEC);
	public static final EntityActionType<OffsetEntityAction> OFFSET = registerInternal("offset", OffsetEntityAction.CODEC, OffsetEntityAction.STREAM_CODEC);
	public static final EntityActionType<RandomChanceEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceEntityAction.CODEC, RandomChanceEntityAction.STREAM_CODEC);
	public static final EntityActionType<ReferenceEntityAction> REFERENCE = registerInternal("reference", ReferenceEntityAction.CODEC, ReferenceEntityAction.STREAM_CODEC);
	public static final EntityActionType<SequenceEntityAction> SEQUENCE = registerInternal("sequence", SequenceEntityAction.CODEC, SequenceEntityAction.STREAM_CODEC);
	public static final EntityActionType<WeightedEntityAction> WEIGHTED = registerInternal("weighted", WeightedEntityAction.CODEC, WeightedEntityAction.STREAM_CODEC);

	public static final EntityActionType<AddExperienceEntityAction> ADD_EXPERIENCE = registerInternal("add_experience", AddExperienceEntityAction.CODEC, AddExperienceEntityAction.STREAM_CODEC);
	public static final EntityActionType<AreaOfEffectEntityAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectEntityAction.CODEC, AreaOfEffectEntityAction.STREAM_CODEC);
	public static final EntityActionType<BlockActionAtEntityAction> BLOCK_ACTION_AT = registerInternal("block_action_at", BlockActionAtEntityAction.CODEC, BlockActionAtEntityAction.STREAM_CODEC);
	public static final EntityActionType<DamageEntityAction> DAMAGE = registerInternal("damage", DamageEntityAction.CODEC, DamageEntityAction.STREAM_CODEC);
	public static final EntityActionType<DismountEntityAction> DISMOUNT = registerInternal("dismount", DismountEntityAction.CODEC, DismountEntityAction.STREAM_CODEC);
	public static final EntityActionType<EmitGameEventEntityAction> EMIT_GAME_EVENT = registerInternal("emit_game_event", EmitGameEventEntityAction.CODEC, EmitGameEventEntityAction.STREAM_CODEC);
	public static final EntityActionType<ExecuteCommandEntityAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandEntityAction.CODEC, ExecuteCommandEntityAction.STREAM_CODEC);
	public static final EntityActionType<ExtinguishEntityAction> EXTINGUISH = registerInternal("extinguish", ExtinguishEntityAction.CODEC, ExtinguishEntityAction.STREAM_CODEC);
	public static final EntityActionType<GiveItemsEntityAction> GIVE_ITEMS = registerInternal("give_items", GiveItemsEntityAction.CODEC, GiveItemsEntityAction.STREAM_CODEC);
	public static final EntityActionType<SetOnFireEntityAction> SET_ON_FIRE = registerInternal("set_on_fire", SetOnFireEntityAction.CODEC, SetOnFireEntityAction.STREAM_CODEC);
	public static final EntityActionType<SwingHandEntityAction> SWING_HAND = registerInternal("swing_hand", SwingHandEntityAction.CODEC, SwingHandEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityAction> EntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends EntityAction> EntityActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return ActionTypes.register(id.withPrefix(EntityActionType.PREFIX), Registry.register(NeoApoliRegistries.ENTITY_ACTION_TYPE, id, new EntityActionType<>(mapCodec, packetCodec)));
	}

}
