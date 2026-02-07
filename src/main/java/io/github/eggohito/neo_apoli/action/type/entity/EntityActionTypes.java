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

	public static final EntityActionType<ChoiceEntityAction> CHOICE = registerInternal("choice", ChoiceEntityAction.MAP_CODEC, ChoiceEntityAction.STREAM_CODEC);
	public static final EntityActionType<ConditionalEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalEntityAction.MAP_CODEC, ConditionalEntityAction.STREAM_CODEC);
	public static final EntityActionType<ExecuteOnEntityEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityEntityAction.MAP_CODEC, ExecuteOnEntityEntityAction.STREAM_CODEC);
	public static final EntityActionType<LoopEntityAction> LOOP = registerInternal("loop", LoopEntityAction.MAP_CODEC, LoopEntityAction.STREAM_CODEC);
	public static final EntityActionType<NothingEntityAction> NOTHING = registerInternal("nothing", NothingEntityAction.MAP_CODEC, NothingEntityAction.STREAM_CODEC);
	public static final EntityActionType<OffsetEntityAction> OFFSET = registerInternal("offset", OffsetEntityAction.MAP_CODEC, OffsetEntityAction.STREAM_CODEC);
	public static final EntityActionType<RandomChanceEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceEntityAction.MAP_CODEC, RandomChanceEntityAction.STREAM_CODEC);
	public static final EntityActionType<ReferenceEntityAction> REFERENCE = registerInternal("reference", ReferenceEntityAction.MAP_CODEC, ReferenceEntityAction.STREAM_CODEC);
	public static final EntityActionType<SequenceEntityAction> SEQUENCE = registerInternal("sequence", SequenceEntityAction.MAP_CODEC, SequenceEntityAction.STREAM_CODEC);
	public static final EntityActionType<SpawnParticlesEntityAction> SPAWN_PARTICLES = registerInternal("spawn_particles", SpawnParticlesEntityAction.MAP_CODEC, SpawnParticlesEntityAction.STREAM_CODEC);
	public static final EntityActionType<WeightedEntityAction> WEIGHTED = registerInternal("weighted", WeightedEntityAction.MAP_CODEC, WeightedEntityAction.STREAM_CODEC);

	public static final EntityActionType<AddExperienceEntityAction> ADD_EXPERIENCE = registerInternal("add_experience", AddExperienceEntityAction.MAP_CODEC, AddExperienceEntityAction.STREAM_CODEC);
	public static final EntityActionType<AddVelocityEntityAction> ADD_VELOCITY = registerInternal("add_velocity", AddVelocityEntityAction.MAP_CODEC, AddVelocityEntityAction.STREAM_CODEC);
	public static final EntityActionType<ApplyEffectsEntityAction> APPLY_EFFECTS = registerInternal("apply_effects", ApplyEffectsEntityAction.MAP_CODEC, ApplyEffectsEntityAction.STREAM_CODEC);
	public static final EntityActionType<AreaOfEffectEntityAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectEntityAction.MAP_CODEC, AreaOfEffectEntityAction.STREAM_CODEC);
	public static final EntityActionType<BlockActionAtEntityAction> BLOCK_ACTION_AT = registerInternal("block_action_at", BlockActionAtEntityAction.MAP_CODEC, BlockActionAtEntityAction.STREAM_CODEC);
	public static final EntityActionType<DamageEntityAction> DAMAGE = registerInternal("damage", DamageEntityAction.MAP_CODEC, DamageEntityAction.STREAM_CODEC);
	public static final EntityActionType<DismountEntityAction> DISMOUNT = registerInternal("dismount", DismountEntityAction.MAP_CODEC, DismountEntityAction.STREAM_CODEC);
	public static final EntityActionType<EmitGameEventEntityAction> EMIT_GAME_EVENT = registerInternal("emit_game_event", EmitGameEventEntityAction.MAP_CODEC, EmitGameEventEntityAction.STREAM_CODEC);
	public static final EntityActionType<ExecuteCommandEntityAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandEntityAction.MAP_CODEC, ExecuteCommandEntityAction.STREAM_CODEC);
	public static final EntityActionType<ExtinguishEntityAction> EXTINGUISH = registerInternal("extinguish", ExtinguishEntityAction.MAP_CODEC, ExtinguishEntityAction.STREAM_CODEC);
	public static final EntityActionType<GiveItemsEntityAction> GIVE_ITEMS = registerInternal("give_items", GiveItemsEntityAction.MAP_CODEC, GiveItemsEntityAction.STREAM_CODEC);
	public static final EntityActionType<SetOnFireEntityAction> SET_ON_FIRE = registerInternal("set_on_fire", SetOnFireEntityAction.MAP_CODEC, SetOnFireEntityAction.STREAM_CODEC);
	public static final EntityActionType<ShootEntityAction> SHOOT = registerInternal("shoot", ShootEntityAction.MAP_CODEC, ShootEntityAction.STREAM_CODEC);
	public static final EntityActionType<SwingHandEntityAction> SWING_HAND = registerInternal("swing_hand", SwingHandEntityAction.MAP_CODEC, SwingHandEntityAction.STREAM_CODEC);
	public static final EntityActionType<TogglePowerEntityAction> TOGGLE_POWER = registerInternal("toggle_power", TogglePowerEntityAction.MAP_CODEC, TogglePowerEntityAction.STREAM_CODEC);
	public static final EntityActionType<TriggerPowerCooldownEntityAction> TRIGGER_POWER_COOLDOWN = registerInternal("trigger_power_cooldown", TriggerPowerCooldownEntityAction.MAP_CODEC, TriggerPowerCooldownEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityAction> EntityActionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends EntityAction> EntityActionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(EntityActionType.PREFIX);
		return ActionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.ENTITY_ACTION_TYPE, prefixedId, new EntityActionType<>(mapCodec, streamCodec)));
	}

}
