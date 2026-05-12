package io.github.eggohito.neo_apoli.registry.action;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.entity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliEntityActionTypes {

	public static final EntityAction.Type<ConditionalEntityAction> CONDITIONAL = registerInternal("conditional", ConditionalEntityAction.MAP_CODEC, ConditionalEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ExecuteOnEntityEntityAction> EXECUTE_ON_ENTITY = registerInternal("execute_on_entity", ExecuteOnEntityEntityAction.MAP_CODEC, ExecuteOnEntityEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<LoopEntityAction> LOOP = registerInternal("loop", LoopEntityAction.MAP_CODEC, LoopEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<NothingEntityAction> NOTHING = registerInternal("nothing", NothingEntityAction.MAP_CODEC, NothingEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<OffsetEntityAction> OFFSET = registerInternal("offset", OffsetEntityAction.MAP_CODEC, OffsetEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<RandomChanceEntityAction> RANDOM_CHANCE = registerInternal("random_chance", RandomChanceEntityAction.MAP_CODEC, RandomChanceEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ReferenceEntityAction> REFERENCE = registerInternal("reference", ReferenceEntityAction.MAP_CODEC, ReferenceEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<SequenceEntityAction> SEQUENCE = registerInternal("sequence", SequenceEntityAction.MAP_CODEC, SequenceEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<SpawnParticlesEntityAction> SPAWN_PARTICLES = registerInternal("spawn_particles", SpawnParticlesEntityAction.MAP_CODEC, SpawnParticlesEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<SwitchEntityAction> SWITCH = registerInternal("switch", SwitchEntityAction.MAP_CODEC, SwitchEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<WeightedEntityAction> WEIGHTED = registerInternal("weighted", WeightedEntityAction.MAP_CODEC, WeightedEntityAction.STREAM_CODEC);

	public static final EntityAction.Type<AddExperienceEntityAction> ADD_EXPERIENCE = registerInternal("add_experience", AddExperienceEntityAction.MAP_CODEC, AddExperienceEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<AddVelocityEntityAction> ADD_VELOCITY = registerInternal("add_velocity", AddVelocityEntityAction.MAP_CODEC, AddVelocityEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ApplyEffectsEntityAction> APPLY_EFFECTS = registerInternal("apply_effects", ApplyEffectsEntityAction.MAP_CODEC, ApplyEffectsEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<AreaOfEffectEntityAction> AREA_OF_EFFECT = registerInternal("area_of_effect", AreaOfEffectEntityAction.MAP_CODEC, AreaOfEffectEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<BlockActionAtEntityAction> BLOCK_ACTION_AT = registerInternal("block_action_at", BlockActionAtEntityAction.MAP_CODEC, BlockActionAtEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<DamageEntityAction> DAMAGE = registerInternal("damage", DamageEntityAction.MAP_CODEC, DamageEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<DismountEntityAction> DISMOUNT = registerInternal("dismount", DismountEntityAction.MAP_CODEC, DismountEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<EmitGameEventEntityAction> EMIT_GAME_EVENT = registerInternal("emit_game_event", EmitGameEventEntityAction.MAP_CODEC, EmitGameEventEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ExecuteCommandEntityAction> EXECUTE_COMMAND = registerInternal("execute_command", ExecuteCommandEntityAction.MAP_CODEC, ExecuteCommandEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ExhaustEntityAction> EXHAUST = registerInternal("exhaust", ExhaustEntityAction.MAP_CODEC, ExhaustEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ExtinguishEntityAction> EXTINGUISH = registerInternal("extinguish", ExtinguishEntityAction.MAP_CODEC, ExtinguishEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<GiveItemsEntityAction> GIVE_ITEMS = registerInternal("give_items", GiveItemsEntityAction.MAP_CODEC, GiveItemsEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<SetOnFireEntityAction> SET_ON_FIRE = registerInternal("set_on_fire", SetOnFireEntityAction.MAP_CODEC, SetOnFireEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<ShootEntityAction> SHOOT = registerInternal("shoot", ShootEntityAction.MAP_CODEC, ShootEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<SwingHandEntityAction> SWING_HAND = registerInternal("swing_hand", SwingHandEntityAction.MAP_CODEC, SwingHandEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<TogglePowerEntityAction> TOGGLE_POWER = registerInternal("toggle_power", TogglePowerEntityAction.MAP_CODEC, TogglePowerEntityAction.STREAM_CODEC);
	public static final EntityAction.Type<TriggerPowerCooldownEntityAction> TRIGGER_POWER_COOLDOWN = registerInternal("trigger_power_cooldown", TriggerPowerCooldownEntityAction.MAP_CODEC, TriggerPowerCooldownEntityAction.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityAction> EntityAction.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends EntityAction> EntityAction.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_ACTION_TYPE, id, new EntityAction.Type<>(mapCodec, streamCodec));
	}

}
