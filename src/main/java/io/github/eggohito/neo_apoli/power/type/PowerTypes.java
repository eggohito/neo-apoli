package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypeUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextType;
import org.apache.commons.lang3.ArrayUtils;

public class PowerTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<PowerType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.POWER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, PowerType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE);

	public static final PowerType<CallbackBlockBreakPower> CALLBACK_BLOCK_BREAK = registerInternal("callback/block/break", CallbackBlockBreakPower.CODEC, CallbackBlockBreakPower.PACKET_CODEC, ContextTypes.BLOCK, ContextTypes.ENTITY);
	public static final PowerType<CallbackPowerAddedPower> CALLBACK_POWER_ADDED = registerInternal("callback/power/added", CallbackPowerAddedPower.CODEC, CallbackPowerAddedPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<CallbackPowerGrantedPower> CALLBACK_POWER_GRANTED = registerInternal("callback/power/granted", CallbackPowerGrantedPower.CODEC, CallbackPowerGrantedPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<CallbackPowerRemovedPower> CALLBACK_POWER_REMOVED = registerInternal("callback/power/removed", CallbackPowerRemovedPower.CODEC, CallbackPowerRemovedPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<CallbackPowerRevokedPower> CALLBACK_POWER_REVOKED = registerInternal("callback/power/revoked", CallbackPowerRevokedPower.CODEC, CallbackPowerRevokedPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<CallbackPlayerRespawnedPower> CALLBACK_PLAYER_RESPAWNED = registerInternal("callback/player/respawned", CallbackPlayerRespawnedPower.CODEC, CallbackPlayerRespawnedPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<CallbackPowerTickPower> CALLBACK_POWER_TICK = registerInternal("callback/power/tick", CallbackPowerTickPower.CODEC, CallbackPowerTickPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<ModifyAttributeLegacyConditionedPower> MODIFY_ATTRIBUTE_LEGACY_CONDITIONED = registerInternal("modify/attribute/legacy/conditioned", ModifyAttributeLegacyConditionedPower.CODEC, ModifyAttributeLegacyConditionedPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<ModifyAttributeLegacyPower> MODIFY_ATTRIBUTE_LEGACY = registerInternal("modify/attribute/legacy", ModifyAttributeLegacyPower.CODEC, ModifyAttributeLegacyPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<ModifyBlockHarvestabilityPower> MODIFY_BLOCK_HARVESTABILITY = registerInternal("modify/block/harvestability", ModifyBlockHarvestabilityPower.CODEC, ModifyBlockHarvestabilityPower.PACKET_CODEC, ContextTypes.BLOCK, ContextTypes.ENTITY);
	public static final PowerType<ModifyBlockSelectabilityPower> MODIFY_BLOCK_SELECTABILITY = registerInternal("modify/block/selectability", ModifyBlockSelectabilityPower.CODEC, ModifyBlockSelectabilityPower.PACKET_CODEC, ContextTypes.BLOCK, ContextTypes.ENTITY);
	public static final PowerType<ModifyBlockUsePower> MODIFY_BLOCK_USE = registerInternal("modify/block/use", ModifyBlockUsePower.CODEC, ModifyBlockUsePower.PACKET_CODEC, ContextTypes.BLOCK, ContextTypes.ENTITY, ContextTypes.ITEM);
	public static final PowerType<ModifyClimbingPower> MODIFY_CLIMBING = registerInternal("modify/climbing", ModifyClimbingPower.CODEC, ModifyClimbingPower.PACKET_CODEC, ContextTypes.BLOCK, ContextTypes.ENTITY);
	public static final PowerType<ModifyEntityTypeTagPower> MODIFY_ENTITY_TYPE_TAG = registerInternal("modify/entity_type_tag", ModifyEntityTypeTagPower.CODEC, ModifyEntityTypeTagPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<ModifyItemUsePower> MODIFY_ITEM_USE = registerInternal("modify/item/use", ModifyItemUsePower.CODEC, ModifyItemUsePower.PACKET_CODEC, ContextTypes.ENTITY, ContextTypes.ITEM);
	public static final PowerType<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal("modify/invisibility", ModifyInvisibilityPower.CODEC, ModifyInvisibilityPower.PACKET_CODEC, ContextTypes.BIENTITY, ContextTypes.ENTITY);
	public static final PowerType<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal("modify/model_color/other", ModifyModelColorOtherPower.CODEC, ModifyModelColorOtherPower.PACKET_CODEC, ContextTypes.BIENTITY, ContextTypes.ENTITY);
	public static final PowerType<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal("modify/model_color/self", ModifyModelColorSelfPower.CODEC, ModifyModelColorSelfPower.PACKET_CODEC, ContextTypes.BIENTITY, ContextTypes.ENTITY);
	public static final PowerType<ModifyShakingPower> MODIFY_SHAKING = registerInternal("modify/shaking", ModifyShakingPower.CODEC, ModifyShakingPower.PACKET_CODEC, ContextTypes.ENTITY);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, MultiplePower.CODEC, MultiplePower.PACKET_CODEC);
	public static final PowerType<CraftingRecipePower> CRAFTING_RECIPE = registerInternal("crafting_recipe", CraftingRecipePower.CODEC, CraftingRecipePower.PACKET_CODEC);
	public static final PowerType<TogglePower> TOGGLE = registerInternal("toggle", TogglePower.CODEC, TogglePower.PACKET_CODEC, ContextTypes.ENTITY);

	public static void registerAll() {
		ALIASES.addPathAlias("attribute", getId(MODIFY_ATTRIBUTE_LEGACY).getPath());
		ALIASES.addPathAlias("action_over_time", getId(CALLBACK_POWER_TICK).getPath());
		ALIASES.addPathAlias("conditioned_attribute", getId(MODIFY_ATTRIBUTE_LEGACY_CONDITIONED).getPath());
		ALIASES.addPathAlias("modify_harvest", getId(MODIFY_BLOCK_HARVESTABILITY).getPath());
		ALIASES.addPathAlias("climbing", getId(MODIFY_CLIMBING).getPath());
		ALIASES.addPathAlias("invisibility", getId(MODIFY_INVISIBILITY).getPath());
		ALIASES.addPathAlias("simple", getId(DUMMY).getPath());
		ALIASES.addPathAlias("shaking", getId(MODIFY_SHAKING).getPath());
	}

	private static <P extends Power> PowerType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec, ContextType... contextTypes) {
		return register(NeoApoli.id(path), mapCodec, packetCodec, contextTypes);
	}

	public static <P extends Power> PowerType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec, ContextType... contextTypes) {
		ContextType[] appended = ArrayUtils.add(contextTypes, ContextTypes.GENERIC);
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(ContextTypeUtil.merge(appended), mapCodec, packetCodec));
	}

	public static Identifier getId(PowerType<?> powerType) {
		return RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, powerType);
	}

}
