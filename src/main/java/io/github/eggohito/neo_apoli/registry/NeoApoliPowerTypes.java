package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.hud.NumberBoundHudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.*;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;

import java.util.function.UnaryOperator;

public class NeoApoliPowerTypes {

	public static final Power.Type<CallbackBlockBreakPower> CALLBACK_BLOCK_BREAK = registerInternal(
		"callback/block/break",
		CallbackBlockBreakPower.CODEC,
		CallbackBlockBreakPower.STREAM_CODEC,
		params -> params
			.required(NeoApoliContextParams.BLOCK)
			.optional(NeoApoliContextParams.DIRECTION)
	);

	public static final Power.Type<CallbackDamageDealtPower> CALLBACK_DAMAGE_DEALT = registerInternal(
		"callback/damage/dealt",
		CallbackDamageDealtPower.CODEC,
		CallbackDamageDealtPower.STREAM_CODEC,
		params -> params
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.required(NeoApoliContextParams.DAMAGE_AMOUNT)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final Power.Type<CallbackPlayerRespawnedPower> CALLBACK_PLAYER_RESPAWNED = registerInternal(
		"callback/player/respawned",
		CallbackPlayerRespawnedPower.CODEC,
		CallbackPlayerRespawnedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<CallbackPlayerWakeUpPower> CALLBACK_PLAYER_WAKE_UP = registerInternal(
		"callback/player/wake_up",
		CallbackPlayerWakeUpPower.CODEC,
		CallbackPlayerWakeUpPower.STREAM_CODEC,
		params -> params.required(NeoApoliContextParams.BLOCK)
	);

	public static final Power.Type<CallbackPowerAddedPower> CALLBACK_POWER_ADDED = registerInternal(
		"callback/power/added",
		CallbackPowerAddedPower.CODEC,
		CallbackPowerAddedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<CallbackPowerGrantedPower> CALLBACK_POWER_GRANTED = registerInternal(
		"callback/power/granted",
		CallbackPowerGrantedPower.CODEC,
		CallbackPowerGrantedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<CallbackPowerRemovedPower> CALLBACK_POWER_REMOVED = registerInternal(
		"callback/power/removed",
		CallbackPowerRemovedPower.CODEC,
		CallbackPowerRemovedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<CallbackPowerRevokedPower> CALLBACK_POWER_REVOKED = registerInternal(
		"callback/power/revoked",
		CallbackPowerRevokedPower.CODEC,
		CallbackPowerRevokedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<CallbackPowerTickPower> CALLBACK_POWER_TICK = registerInternal(
		"callback/power/tick",
		CallbackPowerTickPower.CODEC,
		CallbackPowerTickPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<CallbackProjectileLandPower> CALLBACK_PROJECTILE_LAND = registerInternal(
		"callback/projectile/land",
		CallbackProjectileLandPower.CODEC,
		CallbackProjectileLandPower.STREAM_CODEC,
		params -> params
			.required(NeoApoliContextParams.THIS_ENTITY)
			.required(NeoApoliContextParams.PROJECTILE_ENTITY)
			.required(NeoApoliContextParams.BLOCK)
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.optional(NeoApoliContextParams.TARGET_ENTITY)
			.optional(NeoApoliContextParams.DIRECTION)
	);

	public static final Power.Type<CooldownPower> COOLDOWN = registerInternal(
		"cooldown",
		CooldownPower.CODEC,
		CooldownPower.STREAM_CODEC,
		params -> params
			.required(NumberBoundHudElement.CURRENT_VALUE)
			.required(NumberBoundHudElement.MAX_VALUE)
			.required(NumberBoundHudElement.MIN_VALUE)
	);

	public static final Power.Type<CraftingRecipePower> CRAFTING_RECIPE = registerInternal(
		"crafting_recipe",
		CraftingRecipePower.CODEC,
		CraftingRecipePower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<DummyPower> DUMMY = registerInternal(
		"dummy",
		DummyPower.CODEC,
		DummyPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<HudRenderPower> HUD_RENDER = registerInternal(
		"hud_render",
		HudRenderPower.CODEC,
		HudRenderPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyAirSpeedPower> MODIFY_AIR_SPEED = registerInternal(
		"modify/air/speed",
		ModifyAirSpeedPower.CODEC,
		ModifyAirSpeedPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyAttributeLegacyPower> MODIFY_ATTRIBUTE_LEGACY = registerInternal(
		"modify/attribute/legacy",
		ModifyAttributeLegacyPower.CODEC,
		ModifyAttributeLegacyPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyBlockBreakSpeedPower> MODIFY_BLOCK_BREAK_SPEED = registerInternal(
		"modify/block/break_speed",
		ModifyBlockBreakSpeedPower.CODEC,
		ModifyBlockBreakSpeedPower.STREAM_CODEC,
		params -> params.required(NeoApoliContextParams.BLOCK)
	);

	public static final Power.Type<ModifyBlockHarvestablePower> MODIFY_BLOCK_HARVESTABLE = registerInternal(
		"modify/block/harvestable",
		ModifyBlockHarvestablePower.CODEC,
		ModifyBlockHarvestablePower.STREAM_CODEC,
		params -> params.required(NeoApoliContextParams.BLOCK)
	);

	public static final Power.Type<ModifyBlockSelectablePower> MODIFY_BLOCK_SELECTABLE = registerInternal(
		"modify/block/selectable",
		ModifyBlockSelectablePower.CODEC,
		ModifyBlockSelectablePower.STREAM_CODEC,
		params -> params.required(NeoApoliContextParams.BLOCK)
	);

	public static final Power.Type<ModifyBlockUsePower> MODIFY_BLOCK_USE = registerInternal(
		"modify/block/use",
		ModifyBlockUsePower.CODEC,
		ModifyBlockUsePower.STREAM_CODEC,
		params -> params
			.required(NeoApoliContextParams.THIS_ENTITY)
			.required(NeoApoliContextParams.BLOCK)
			.required(NeoApoliContextParams.SLOT)
			.required(NeoApoliContextParams.ITEM)
			.required(NeoApoliContextParams.DIRECTION)
	);

	public static final Power.Type<ModifyClimbingPower> MODIFY_CLIMBING = registerInternal(
		"modify/climbing",
		ModifyClimbingPower.CODEC,
		ModifyClimbingPower.STREAM_CODEC,
		params -> params.required(NeoApoliContextParams.BLOCK)
	);

	public static final Power.Type<ModifyDamageDealtPower> MODIFY_DAMAGE_DEALT = registerInternal(
		"modify/damage/dealt",
		ModifyDamageDealtPower.CODEC,
		ModifyDamageDealtPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.required(NeoApoliContextParams.DAMAGE_AMOUNT)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final Power.Type<ModifyDamageInvulnerabilityPower> MODIFY_DAMAGE_INVULNERABILITY = registerInternal(
		"modify/damage/invulnerability",
		ModifyDamageInvulnerabilityPower.CODEC,
		ModifyDamageInvulnerabilityPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final Power.Type<ModifyDamageTakenPower> MODIFY_DAMAGE_TAKEN = registerInternal(
		"modify/damage/taken",
		ModifyDamageTakenPower.CODEC,
		ModifyDamageTakenPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.DAMAGE_SOURCE)
			.required(NeoApoliContextParams.DAMAGE_AMOUNT)
			.optional(NeoApoliContextParams.DAMAGING_ENTITY)
			.optional(NeoApoliContextParams.DIRECT_DAMAGING_ENTITY)
	);

	public static final Power.Type<ModifyEffectDurationPower> MODIFY_EFFECT_DURATION = registerInternal(
		"modify/effect/duration",
		ModifyEffectDurationPower.CODEC,
		ModifyEffectDurationPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.EFFECT)
	);

	public static final Power.Type<ModifyEffectImmunityPower> MODIFY_EFFECT_IMMUNITY = registerInternal(
		"modify/effect/immunity",
		ModifyEffectImmunityPower.CODEC,
		ModifyEffectImmunityPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
			.required(NeoApoliContextParams.EFFECT)
	);

	public static final Power.Type<ModifyElytraFlightPower> MODIFY_ELYTRA_FLIGHT = registerInternal(
		"modify/elytra/flight",
		ModifyElytraFlightPower.CODEC,
		ModifyElytraFlightPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyElytraRenderPower> MODIFY_ELYTRA_RENDER = registerInternal(
		"modify/elytra/render",
		ModifyElytraRenderPower.CODEC,
		ModifyElytraRenderPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyEntityTypeTagPower> MODIFY_ENTITY_TYPE_TAG = registerInternal(
		"modify/entity/type_tag",
		ModifyEntityTypeTagPower.CODEC,
		ModifyEntityTypeTagPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyFallingPower> MODIFY_FALLING = registerInternal(
		"modify/falling",
		ModifyFallingPower.CODEC,
		ModifyFallingPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyGlowingOtherPower> MODIFY_GLOWING_OTHER = registerInternal(
		"modify/glowing/other",
		ModifyGlowingOtherPower.CODEC,
		ModifyGlowingOtherPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final Power.Type<ModifyGlowingSelfPower> MODIFY_GLOWING_SELF = registerInternal(
		"modify/glowing/self",
		ModifyGlowingSelfPower.CODEC,
		ModifyGlowingSelfPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final Power.Type<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal(
		"modify/invisibility",
		ModifyInvisibilityPower.CODEC,
		ModifyInvisibilityPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final Power.Type<ModifyItemUsePower> MODIFY_ITEM_USE = registerInternal(
		"modify/item/use",
		ModifyItemUsePower.CODEC,
		ModifyItemUsePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.SLOT)
			.required(NeoApoliContextParams.ITEM)
	);

	public static final Power.Type<ModifyItemWearablePower> MODIFY_ITEM_WEARABLE = registerInternal(
		"modify/item/wearable",
		ModifyItemWearablePower.CODEC,
		ModifyItemWearablePower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ITEM)
	);

	public static final Power.Type<ModifyJumpPower> MODIFY_JUMP = registerInternal(
		"modify/jump",
		ModifyJumpPower.CODEC,
		ModifyJumpPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal(
		"modify/model/color/other",
		ModifyModelColorOtherPower.CODEC,
		ModifyModelColorOtherPower.STREAM_CODEC,
		keys -> keys
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final Power.Type<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal(
		"modify/model/color/self",
		ModifyModelColorSelfPower.CODEC,
		ModifyModelColorSelfPower.STREAM_CODEC,
		keys -> keys
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.required(NeoApoliContextParams.TARGET_ENTITY)
	);

	public static final Power.Type<ModifyModelShakingPower> MODIFY_MODEL_SHAKING = registerInternal(
		"modify/model/shaking",
		ModifyModelShakingPower.CODEC,
		ModifyModelShakingPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<ModifyPlayerSpawnPower> MODIFY_PLAYER_SPAWN = registerInternal(
		"modify/player/spawn",
		ModifyPlayerSpawnPower.CODEC,
		ModifyPlayerSpawnPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<MultiplePower> MULTIPLE = registerInternal(
		"multiple",
		MultiplePower.CODEC,
		MultiplePower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<NbtPower> NBT = registerInternal(
		"nbt",
		NbtPower.CODEC,
		NbtPower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static final Power.Type<PhasingPower> PHASING = registerInternal(
		"phasing",
		PhasingPower.CODEC,
		PhasingPower.STREAM_CODEC,
		params -> params.required(NeoApoliContextParams.BLOCK)
	);

	public static final Power.Type<ReplaceLootTablePower> REPLACE_LOOT_TABLE = registerInternal(
		"replace_loot_table",
		ReplaceLootTablePower.CODEC,
		ReplaceLootTablePower.STREAM_CODEC,
		builder -> builder
			.required(NeoApoliContextParams.ACTOR_ENTITY)
			.optional(NeoApoliContextParams.TARGET_ENTITY)
			.optional(NeoApoliContextParams.BLOCK)
			.optional(NeoApoliContextParams.ITEM)
	);

	public static final Power.Type<TogglePower> TOGGLE = registerInternal(
		"toggle",
		TogglePower.CODEC,
		TogglePower.STREAM_CODEC,
		UnaryOperator.identity()
	);

	public static void registerAll() {

	}

	private static <P extends Power> Power.Type<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec, UnaryOperator<ContextKeySet.Builder> parametersBuilder) {
		return register(NeoApoli.id(path), mapCodec, streamCodec, parametersBuilder);
	}

	public static <P extends Power> Power.Type<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec, UnaryOperator<ContextKeySet.Builder> parametersBuilder) {

		ContextKeySet parameters = parametersBuilder.apply(new ContextKeySet.Builder())
			.required(NeoApoliContextParams.THIS_ENTITY)
			.build();

		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new Power.Type<>(parameters, mapCodec, streamCodec));

	}

}
