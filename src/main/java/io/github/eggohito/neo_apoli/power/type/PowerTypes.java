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

	public static final PowerType<BlockBreakPower> BLOCK_BREAK = registerInternal("block_break", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), BlockBreakPower.CODEC, BlockBreakPower.PACKET_CODEC);
	public static final PowerType<BlockInteractPower> BLOCK_INTERACT = registerInternal("block_interact", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY, ContextTypes.ITEM), BlockInteractPower.CODEC, BlockInteractPower.PACKET_CODEC);
	public static final PowerType<CallbackPower> CALLBACK = registerInternal("callback", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPower.CODEC, CallbackPower.PACKET_CODEC);
	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", ContextTypes.GENERIC, DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<ModifyAttributeLegacyConditionedPower> MODIFY_ATTRIBUTE_LEGACY_CONDITIONED = registerInternal("modify/attribute/legacy/conditioned", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyAttributeLegacyConditionedPower.CODEC, ModifyAttributeLegacyConditionedPower.PACKET_CODEC);
	public static final PowerType<ModifyAttributeLegacyPower> MODIFY_ATTRIBUTE_LEGACY = registerInternal("modify/attribute/legacy", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyAttributeLegacyPower.CODEC, ModifyAttributeLegacyPower.PACKET_CODEC);
	public static final PowerType<ModifyBlockHarvestabilityPower> MODIFY_BLOCK_HARVESTABILITY = registerInternal("modify/block_harvestability", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), ModifyBlockHarvestabilityPower.CODEC, ModifyBlockHarvestabilityPower.PACKET_CODEC);
	public static final PowerType<ModifyClimbingPower> MODIFY_CLIMBING = registerInternal("modify/climbing", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), ModifyClimbingPower.CODEC, ModifyClimbingPower.PACKET_CODEC);
	public static final PowerType<ModifyEntityTypeTagPower> MODIFY_ENTITY_TYPE_TAG = registerInternal("modify/entity_type_tag", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyEntityTypeTagPower.CODEC, ModifyEntityTypeTagPower.PACKET_CODEC);
	public static final PowerType<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal("modify/invisibility", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyInvisibilityPower.CODEC, ModifyInvisibilityPower.PACKET_CODEC);
	public static final PowerType<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal("modify/model_color/other", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyModelColorOtherPower.CODEC, ModifyModelColorOtherPower.PACKET_CODEC);
	public static final PowerType<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal("modify/model_color/self", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyModelColorSelfPower.CODEC, ModifyModelColorSelfPower.PACKET_CODEC);
	public static final PowerType<ModifyShakingPower> MODIFY_SHAKING = registerInternal("modify/shaking", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyShakingPower.CODEC, ModifyShakingPower.PACKET_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, ContextTypes.GENERIC, MultiplePower.CODEC, MultiplePower.PACKET_CODEC);
	public static final PowerType<PreventItemUsePower> PREVENT_ITEM_USE = registerInternal("prevent/item_use", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY, ContextTypes.ITEM), PreventItemUsePower.CODEC, PreventItemUsePower.PACKET_CODEC);
	public static final PowerType<TickingPower> TICKING = registerInternal("ticking", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), TickingPower.CODEC, TickingPower.PACKET_CODEC);

	public static void registerAll() {
		ALIASES.addPathAlias("attribute", getId(MODIFY_ATTRIBUTE_LEGACY).getPath());
		ALIASES.addPathAlias("action_on_callback", getId(CALLBACK).getPath());
		ALIASES.addPathAlias("action_over_time", getId(TICKING).getPath());
		ALIASES.addPathAlias("conditioned_attribute", getId(MODIFY_ATTRIBUTE_LEGACY_CONDITIONED).getPath());
		ALIASES.addPathAlias("modify_harvest", getId(MODIFY_BLOCK_HARVESTABILITY).getPath());
		ALIASES.addPathAlias("climbing", getId(MODIFY_CLIMBING).getPath());
		ALIASES.addPathAlias("invisibility", getId(MODIFY_INVISIBILITY).getPath());
		ALIASES.addPathAlias("simple", getId(DUMMY).getPath());
		ALIASES.addPathAlias("shaking", getId(MODIFY_SHAKING).getPath());
		ALIASES.addPathAlias("prevent_item_use", getId(PREVENT_ITEM_USE).getPath());
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
