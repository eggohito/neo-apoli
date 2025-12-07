package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import org.apache.commons.lang3.ArrayUtils;

public class PowerTypes {

	public static final PowerType<CallbackBlockBreakPower> CALLBACK_BLOCK_BREAK = registerInternal("callback/block/break", CallbackBlockBreakPower.CODEC, CallbackBlockBreakPower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK);
	public static final PowerType<CallbackDamageDealtPower> CALLBACK_DAMAGE_DEALT = registerInternal("callback/damage/dealt", CallbackDamageDealtPower.CODEC, CallbackDamageDealtPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY, NeoApoliContextKeySets.DAMAGE);
	public static final PowerType<CallbackPlayerRespawnedPower> CALLBACK_PLAYER_RESPAWNED = registerInternal("callback/player/respawned", CallbackPlayerRespawnedPower.CODEC, CallbackPlayerRespawnedPower.STREAM_CODEC);
	public static final PowerType<CallbackPlayerWakeUpPower> CALLBACK_PLAYER_WAKE_UP = registerInternal("callback/player/wake_up", CallbackPlayerWakeUpPower.CODEC, CallbackPlayerWakeUpPower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK);
	public static final PowerType<CallbackPowerAddedPower> CALLBACK_POWER_ADDED = registerInternal("callback/power/added", CallbackPowerAddedPower.CODEC, CallbackPowerAddedPower.STREAM_CODEC);
	public static final PowerType<CallbackPowerGrantedPower> CALLBACK_POWER_GRANTED = registerInternal("callback/power/granted", CallbackPowerGrantedPower.CODEC, CallbackPowerGrantedPower.STREAM_CODEC);
	public static final PowerType<CallbackPowerRemovedPower> CALLBACK_POWER_REMOVED = registerInternal("callback/power/removed", CallbackPowerRemovedPower.CODEC, CallbackPowerRemovedPower.STREAM_CODEC);
	public static final PowerType<CallbackPowerRevokedPower> CALLBACK_POWER_REVOKED = registerInternal("callback/power/revoked", CallbackPowerRevokedPower.CODEC, CallbackPowerRevokedPower.STREAM_CODEC);
	public static final PowerType<CallbackPowerTickPower> CALLBACK_POWER_TICK = registerInternal("callback/power/tick", CallbackPowerTickPower.CODEC, CallbackPowerTickPower.STREAM_CODEC);
	public static final PowerType<CooldownPower> COOLDOWN = registerInternal("cooldown", CooldownPower.CODEC, CooldownPower.STREAM_CODEC, NeoApoliContextKeySets.NUMBER_BOUND);
	public static final PowerType<CraftingRecipePower> CRAFTING_RECIPE = registerInternal("crafting_recipe", CraftingRecipePower.CODEC, CraftingRecipePower.STREAM_CODEC);
	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", DummyPower.CODEC, DummyPower.STREAM_CODEC);
	public static final PowerType<HudRenderPower> HUD_RENDER = registerInternal("hud_render", HudRenderPower.CODEC, HudRenderPower.STREAM_CODEC);
	public static final PowerType<ModifyAirSpeedPower> MODIFY_AIR_SPEED = registerInternal("modify/air/speed", ModifyAirSpeedPower.CODEC, ModifyAirSpeedPower.STREAM_CODEC);
	public static final PowerType<ModifyAttributeLegacyConditionalPower> MODIFY_ATTRIBUTE_LEGACY_CONDITIONAL = registerInternal("modify/attribute/legacy/conditional", ModifyAttributeLegacyConditionalPower.CODEC, ModifyAttributeLegacyConditionalPower.STREAM_CODEC);
	public static final PowerType<ModifyAttributeLegacyPower> MODIFY_ATTRIBUTE_LEGACY = registerInternal("modify/attribute/legacy", ModifyAttributeLegacyPower.CODEC, ModifyAttributeLegacyPower.STREAM_CODEC);
	public static final PowerType<ModifyBlockHarvestablePower> MODIFY_BLOCK_HARVESTABLE = registerInternal("modify/block/harvestable", ModifyBlockHarvestablePower.CODEC, ModifyBlockHarvestablePower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK);
	public static final PowerType<ModifyBlockSelectablePower> MODIFY_BLOCK_SELECTABLE = registerInternal("modify/block/selectable", ModifyBlockSelectablePower.CODEC, ModifyBlockSelectablePower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK);
	public static final PowerType<ModifyBlockUsePower> MODIFY_BLOCK_USE = registerInternal("modify/block/use", ModifyBlockUsePower.CODEC, ModifyBlockUsePower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK, NeoApoliContextKeySets.ITEM);
	public static final PowerType<ModifyClimbingPower> MODIFY_CLIMBING = registerInternal("modify/climbing", ModifyClimbingPower.CODEC, ModifyClimbingPower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK);
	public static final PowerType<ModifyDamageDealtPower> MODIFY_DAMAGE_DEALT = registerInternal("modify/damage/dealt", ModifyDamageDealtPower.CODEC, ModifyDamageDealtPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY, NeoApoliContextKeySets.DAMAGE);
	public static final PowerType<ModifyDamageTakenPower> MODIFY_DAMAGE_TAKEN = registerInternal("modify/damage/taken", ModifyDamageTakenPower.CODEC, ModifyDamageTakenPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY, NeoApoliContextKeySets.DAMAGE);
	public static final PowerType<ModifyEntityTypeTagPower> MODIFY_ENTITY_TYPE_TAG = registerInternal("modify/entity/type_tag", ModifyEntityTypeTagPower.CODEC, ModifyEntityTypeTagPower.STREAM_CODEC);
	public static final PowerType<ModifyFallingPower> MODIFY_FALLING = registerInternal("modify/falling", ModifyFallingPower.CODEC, ModifyFallingPower.STREAM_CODEC);
	public static final PowerType<ModifyGlowingOtherPower> MODIFY_GLOWING_OTHER_POWER = registerInternal("modify/glowing/other", ModifyGlowingOtherPower.CODEC, ModifyGlowingOtherPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY);
	public static final PowerType<ModifyGlowingSelfPower> MODIFY_GLOWING_SELF_POWER = registerInternal("modify/glowing/self", ModifyGlowingSelfPower.CODEC, ModifyGlowingSelfPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY);
	public static final PowerType<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal("modify/invisibility", ModifyInvisibilityPower.CODEC, ModifyInvisibilityPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY);
	public static final PowerType<ModifyInvulnerabilityPower> MODIFY_INVULNERABILITY = registerInternal("modify/invulnerability", ModifyInvulnerabilityPower.CODEC, ModifyInvulnerabilityPower.STREAM_CODEC, NeoApoliContextKeySets.DAMAGE_WITHOUT_AMOUNT);
	public static final PowerType<ModifyItemUsePower> MODIFY_ITEM_USE = registerInternal("modify/item/use", ModifyItemUsePower.CODEC, ModifyItemUsePower.STREAM_CODEC, NeoApoliContextKeySets.ITEM);
	public static final PowerType<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal("modify/model/color/other", ModifyModelColorOtherPower.CODEC, ModifyModelColorOtherPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY);
	public static final PowerType<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal("modify/model/color/self", ModifyModelColorSelfPower.CODEC, ModifyModelColorSelfPower.STREAM_CODEC, NeoApoliContextKeySets.BIENTITY);
	public static final PowerType<ModifyModelShakingPower> MODIFY_MODEL_SHAKING = registerInternal("modify/model/shaking", ModifyModelShakingPower.CODEC, ModifyModelShakingPower.STREAM_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, MultiplePower.CODEC, MultiplePower.STREAM_CODEC);
	public static final PowerType<PhasingPower> PHASING = registerInternal("phasing", PhasingPower.CODEC, PhasingPower.STREAM_CODEC, NeoApoliContextKeySets.BLOCK, NeoApoliContextKeySets.ENTITY);
	public static final PowerType<TogglePower> TOGGLE = registerInternal("toggle", TogglePower.CODEC, TogglePower.STREAM_CODEC, NeoApoliContextKeySets.ENTITY);

	public static void registerAll() {

	}

	private static <P extends Power> PowerType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec, ContextKeySet... contextTypes) {
		return register(NeoApoli.id(path), mapCodec, packetCodec, contextTypes);
	}

	public static <P extends Power> PowerType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec, ContextKeySet... contextTypes) {
		ContextKeySet[] appended = ArrayUtils.addAll(contextTypes, NeoApoliContextKeySets.GENERIC, NeoApoliContextKeySets.ENTITY);
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(ContextKeySetHelper.merge(appended), mapCodec, packetCodec));
	}

}
