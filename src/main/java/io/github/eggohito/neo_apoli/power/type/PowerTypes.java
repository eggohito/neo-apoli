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
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextType;

public class PowerTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<PowerType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.POWER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, PowerType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE);

	public static final PowerType<CallbackBlockBreakPower> CALLBACK_BLOCK_BREAK = registerInternal("callback/block/break", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), CallbackBlockBreakPower.CODEC, CallbackBlockBreakPower.PACKET_CODEC);
	public static final PowerType<CallbackPowerAddedPower> CALLBACK_POWER_ADDED = registerInternal("callback/power/added", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPowerAddedPower.CODEC, CallbackPowerAddedPower.PACKET_CODEC);
	public static final PowerType<CallbackPowerGrantedPower> CALLBACK_POWER_GRANTED = registerInternal("callback/power/granted", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPowerGrantedPower.CODEC, CallbackPowerGrantedPower.PACKET_CODEC);
	public static final PowerType<CallbackPowerRemovedPower> CALLBACK_POWER_REMOVED = registerInternal("callback/power/removed", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPowerRemovedPower.CODEC, CallbackPowerRemovedPower.PACKET_CODEC);
	public static final PowerType<CallbackPowerRevokedPower> CALLBACK_POWER_REVOKED = registerInternal("callback/power/revoked", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPowerRevokedPower.CODEC, CallbackPowerRevokedPower.PACKET_CODEC);
	public static final PowerType<CallbackPlayerRespawnedPower> CALLBACK_PLAYER_RESPAWNED = registerInternal("callback/player/respawned", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPlayerRespawnedPower.CODEC, CallbackPlayerRespawnedPower.PACKET_CODEC);
	public static final PowerType<CallbackPowerTickPower> CALLBACK_POWER_TICK = registerInternal("callback/power/tick", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPowerTickPower.CODEC, CallbackPowerTickPower.PACKET_CODEC);
	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", ContextTypes.GENERIC, DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<ModifyAttributeLegacyConditionedPower> MODIFY_ATTRIBUTE_LEGACY_CONDITIONED = registerInternal("modify/attribute/legacy/conditioned", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyAttributeLegacyConditionedPower.CODEC, ModifyAttributeLegacyConditionedPower.PACKET_CODEC);
	public static final PowerType<ModifyAttributeLegacyPower> MODIFY_ATTRIBUTE_LEGACY = registerInternal("modify/attribute/legacy", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyAttributeLegacyPower.CODEC, ModifyAttributeLegacyPower.PACKET_CODEC);
	public static final PowerType<ModifyBlockHarvestabilityPower> MODIFY_BLOCK_HARVESTABILITY = registerInternal("modify/block/harvestability", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), ModifyBlockHarvestabilityPower.CODEC, ModifyBlockHarvestabilityPower.PACKET_CODEC);
	public static final PowerType<ModifyBlockSelectabilityPower> MODIFY_BLOCK_SELECTABILITY = registerInternal("modify/block/selectability", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), ModifyBlockSelectabilityPower.CODEC, ModifyBlockSelectabilityPower.PACKET_CODEC);
	public static final PowerType<ModifyBlockUsePower> MODIFY_BLOCK_USE = registerInternal("modify/block/use", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY, ContextTypes.ITEM), ModifyBlockUsePower.CODEC, ModifyBlockUsePower.PACKET_CODEC);
	public static final PowerType<ModifyClimbingPower> MODIFY_CLIMBING = registerInternal("modify/climbing", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), ModifyClimbingPower.CODEC, ModifyClimbingPower.PACKET_CODEC);
	public static final PowerType<ModifyEntityTypeTagPower> MODIFY_ENTITY_TYPE_TAG = registerInternal("modify/entity_type_tag", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyEntityTypeTagPower.CODEC, ModifyEntityTypeTagPower.PACKET_CODEC);
	public static final PowerType<ModifyItemUsePower> MODIFY_ITEM_USE = registerInternal("modify/item/use", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY, ContextTypes.ITEM), ModifyItemUsePower.CODEC, ModifyItemUsePower.PACKET_CODEC);
	public static final PowerType<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal("modify/invisibility", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyInvisibilityPower.CODEC, ModifyInvisibilityPower.PACKET_CODEC);
	public static final PowerType<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal("modify/model_color/other", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyModelColorOtherPower.CODEC, ModifyModelColorOtherPower.PACKET_CODEC);
	public static final PowerType<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal("modify/model_color/self", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyModelColorSelfPower.CODEC, ModifyModelColorSelfPower.PACKET_CODEC);
	public static final PowerType<ModifyShakingPower> MODIFY_SHAKING = registerInternal("modify/shaking", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyShakingPower.CODEC, ModifyShakingPower.PACKET_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), MultiplePower.CODEC, MultiplePower.PACKET_CODEC);
	public static final PowerType<TogglePower> TOGGLE = registerInternal("toggle", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), TogglePower.CODEC, TogglePower.PACKET_CODEC);

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

	private static <P extends Power> PowerType<P> registerInternal(String path, ContextType contextType, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), contextType, mapCodec, packetCodec);
	}

	public static Identifier getId(PowerType<?> powerType) {
		return RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, powerType);
	}

	public static <P extends Power> PowerType<P> register(Identifier id, ContextType contextType, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(contextType, mapCodec, packetCodec));
	}

}
