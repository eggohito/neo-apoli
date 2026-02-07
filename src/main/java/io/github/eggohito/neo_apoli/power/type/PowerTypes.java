package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;

import java.util.function.UnaryOperator;

public class PowerTypes {

	public static final PowerType<CallbackBlockBreakPower> CALLBACK_BLOCK_BREAK = registerInternal(
		"callback/block/break",
		CallbackBlockBreakPower.MAP_CODEC,
		CallbackBlockBreakPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
			.optional(NeoApoliContextParams.DIRECTION)
	);

	public static final PowerType<CallbackDamageDealtPower> CALLBACK_DAMAGE_DEALT = registerInternal(
		"callback/damage/dealt",
		CallbackDamageDealtPower.MAP_CODEC,
		CallbackDamageDealtPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.required(NeoApoliContextParams.DAMAGE_AMOUNT)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final PowerType<CallbackPlayerRespawnedPower> CALLBACK_PLAYER_RESPAWNED = registerInternal(
		"callback/player/respawned",
		CallbackPlayerRespawnedPower.MAP_CODEC,
		CallbackPlayerRespawnedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<CallbackPlayerWakeUpPower> CALLBACK_PLAYER_WAKE_UP = registerInternal(
		"callback/player/wake_up",
		CallbackPlayerWakeUpPower.MAP_CODEC,
		CallbackPlayerWakeUpPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
	);

	public static final PowerType<CallbackPowerAddedPower> CALLBACK_POWER_ADDED = registerInternal(
		"callback/power/added",
		CallbackPowerAddedPower.MAP_CODEC,
		CallbackPowerAddedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<CallbackPowerGrantedPower> CALLBACK_POWER_GRANTED = registerInternal(
		"callback/power/granted",
		CallbackPowerGrantedPower.MAP_CODEC,
		CallbackPowerGrantedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<CallbackPowerRemovedPower> CALLBACK_POWER_REMOVED = registerInternal(
		"callback/power/removed",
		CallbackPowerRemovedPower.MAP_CODEC,
		CallbackPowerRemovedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<CallbackPowerRevokedPower> CALLBACK_POWER_REVOKED = registerInternal(
		"callback/power/revoked",
		CallbackPowerRevokedPower.MAP_CODEC,
		CallbackPowerRevokedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<CallbackPowerTickPower> CALLBACK_POWER_TICK = registerInternal(
		"callback/power/tick",
		CallbackPowerTickPower.MAP_CODEC,
		CallbackPowerTickPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<CallbackProjectileLandPower> CALLBACK_PROJECTILE_LAND = registerInternal(
		"callback/projectile/land",
		CallbackProjectileLandPower.MAP_CODEC,
		CallbackProjectileLandPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.optional(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
			.optional(NeoApoliContextParams.DIRECTION)
			.required(NeoApoliContextParams.PROJECTILE_ENTITY)
	);

	public static final PowerType<CooldownPower> COOLDOWN = registerInternal(
		"cooldown",
		CooldownPower.MAP_CODEC,
		CooldownPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.MIN_VALUE)
			.required(NeoApoliContextParams.MAX_VALUE)
			.required(NeoApoliContextParams.CURRENT_VALUE)
	);

	public static final PowerType<CraftingRecipePower> CRAFTING_RECIPE = registerInternal(
		"crafting_recipe",
		CraftingRecipePower.MAP_CODEC,
		CraftingRecipePower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<DummyPower> DUMMY = registerInternal(
		"dummy",
		DummyPower.MAP_CODEC,
		DummyPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<HudRenderPower> HUD_RENDER = registerInternal(
		"hud_render",
		HudRenderPower.MAP_CODEC,
		HudRenderPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyAirSpeedPower> MODIFY_AIR_SPEED = registerInternal(
		"modify/air/speed",
		ModifyAirSpeedPower.MAP_CODEC,
		ModifyAirSpeedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyAttributeLegacyPower> MODIFY_ATTRIBUTE_LEGACY = registerInternal(
		"modify/attribute/legacy",
		ModifyAttributeLegacyPower.MAP_CODEC,
		ModifyAttributeLegacyPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyBlockHarvestablePower> MODIFY_BLOCK_HARVESTABLE = registerInternal(
		"modify/block/harvestable",
		ModifyBlockHarvestablePower.MAP_CODEC,
		ModifyBlockHarvestablePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
	);

	public static final PowerType<ModifyBlockSelectablePower> MODIFY_BLOCK_SELECTABLE = registerInternal(
		"modify/block/selectable",
		ModifyBlockSelectablePower.MAP_CODEC,
		ModifyBlockSelectablePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
	);

	public static final PowerType<ModifyBlockUsePower> MODIFY_BLOCK_USE = registerInternal(
		"modify/block/use",
		ModifyBlockUsePower.MAP_CODEC,
		ModifyBlockUsePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
			.required(NeoApoliContextParams.DIRECTION)
			.required(NeoApoliContextParams.SLOT_ACCESS)
			.required(NeoApoliContextParams.ITEM_STACK)
	);

	public static final PowerType<ModifyClimbingPower> MODIFY_CLIMBING = registerInternal(
		"modify/climbing",
		ModifyClimbingPower.MAP_CODEC,
		ModifyClimbingPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
	);

	public static final PowerType<ModifyDamageDealtPower> MODIFY_DAMAGE_DEALT = registerInternal(
		"modify/damage/dealt",
		ModifyDamageDealtPower.MAP_CODEC,
		ModifyDamageDealtPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.required(NeoApoliContextParams.DAMAGE_AMOUNT)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final PowerType<ModifyDamageInvulnerabilityPower> MODIFY_DAMAGE_INVULNERABILITY = registerInternal(
		"modify/damage/invulnerability",
		ModifyDamageInvulnerabilityPower.MAP_CODEC,
		ModifyDamageInvulnerabilityPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final PowerType<ModifyDamageTakenPower> MODIFY_DAMAGE_TAKEN = registerInternal(
		"modify/damage/taken",
		ModifyDamageTakenPower.MAP_CODEC,
		ModifyDamageTakenPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.required(NeoApoliContextParams.DAMAGE_AMOUNT)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final PowerType<ModifyEffectDurationPower> MODIFY_EFFECT_DURATION = registerInternal(
		"modify/effect/duration",
		ModifyEffectDurationPower.MAP_CODEC,
		ModifyEffectDurationPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.EFFECT_INSTANCE)
	);

	public static final PowerType<ModifyEffectImmunityPower> MODIFY_EFFECT_IMMUNITY = registerInternal(
		"modify/effect/immunity",
		ModifyEffectImmunityPower.MAP_CODEC,
		ModifyEffectImmunityPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.EFFECT_INSTANCE)
	);

	public static final PowerType<ModifyElytraFlightPower> MODIFY_ELYTRA_FLIGHT = registerInternal(
		"modify/elytra/flight",
		ModifyElytraFlightPower.MAP_CODEC,
		ModifyElytraFlightPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyElytraRenderPower> MODIFY_ELYTRA_RENDER = registerInternal(
		"modify/elytra/render",
		ModifyElytraRenderPower.MAP_CODEC,
		ModifyElytraRenderPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyEntityTypeTagPower> MODIFY_ENTITY_TYPE_TAG = registerInternal(
		"modify/entity/type_tag",
		ModifyEntityTypeTagPower.MAP_CODEC,
		ModifyEntityTypeTagPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyFallingPower> MODIFY_FALLING = registerInternal(
		"modify/falling",
		ModifyFallingPower.MAP_CODEC,
		ModifyFallingPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyGlowingOtherPower> MODIFY_GLOWING_OTHER = registerInternal(
		"modify/glowing/other",
		ModifyGlowingOtherPower.MAP_CODEC,
		ModifyGlowingOtherPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final PowerType<ModifyGlowingSelfPower> MODIFY_GLOWING_SELF = registerInternal(
		"modify/glowing/self",
		ModifyGlowingSelfPower.MAP_CODEC,
		ModifyGlowingSelfPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final PowerType<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal(
		"modify/invisibility",
		ModifyInvisibilityPower.MAP_CODEC,
		ModifyInvisibilityPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final PowerType<ModifyItemUsePower> MODIFY_ITEM_USE = registerInternal(
		"modify/item/use",
		ModifyItemUsePower.MAP_CODEC,
		ModifyItemUsePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.SLOT_ACCESS)
			.required(NeoApoliContextParams.ITEM_STACK)
	);

	public static final PowerType<ModifyItemWearablePower> MODIFY_ITEM_WEARABLE = registerInternal(
		"modify/item/wearable",
		ModifyItemWearablePower.MAP_CODEC,
		ModifyItemWearablePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ITEM_STACK)
	);

	public static final PowerType<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal(
		"modify/model/color/other",
		ModifyModelColorOtherPower.MAP_CODEC,
		ModifyModelColorOtherPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final PowerType<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal(
		"modify/model/color/self",
		ModifyModelColorSelfPower.MAP_CODEC,
		ModifyModelColorSelfPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final PowerType<ModifyModelShakingPower> MODIFY_MODEL_SHAKING = registerInternal(
		"modify/model/shaking",
		ModifyModelShakingPower.MAP_CODEC,
		ModifyModelShakingPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<ModifyPlayerSpawnPower> MODIFY_PLAYER_SPAWN = registerInternal(
		"modify/player/spawn",
		ModifyPlayerSpawnPower.MAP_CODEC,
		ModifyPlayerSpawnPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<MultiplePower> MULTIPLE = registerInternal(
		"multiple",
		MultiplePower.MAP_CODEC,
		MultiplePower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<NbtPower> NBT = registerInternal(
		"nbt",
		NbtPower.MAP_CODEC,
		NbtPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final PowerType<PhasingPower> PHASING = registerInternal(
		"phasing",
		PhasingPower.MAP_CODEC,
		PhasingPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.BLOCK_POS)
			.required(NeoApoliContextParams.BLOCK_STATE)
			.optional(NeoApoliContextParams.BLOCK_ENTITY)
	);

	public static final PowerType<TogglePower> TOGGLE = registerInternal(
		"toggle",
		TogglePower.MAP_CODEC,
		TogglePower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static void registerAll() {

	}

	private static <P extends Power> PowerType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec, UnaryOperator<ContextKeySet.Builder> builderBuilder) {
		return register(NeoApoli.id(path), mapCodec, streamCodec, builderBuilder);
	}

	public static <P extends Power> PowerType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec, UnaryOperator<ContextKeySet.Builder> builderBuilder) {

		ContextKeySet keySet = builderBuilder
			.apply(new ContextKeySet.Builder())
			.required(NeoApoliContextParams.THIS_ENTITY)
			.required(NeoApoliContextParams.THIS_POS)
			.build();

		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(keySet, mapCodec, streamCodec));

	}

}
