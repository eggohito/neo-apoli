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
	public static final PowerType<BlockHarvestPower> BLOCK_HARVEST = registerInternal("block_harvest", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY), BlockHarvestPower.CODEC, BlockHarvestPower.PACKET_CODEC);
	public static final PowerType<BlockInteractPower> BLOCK_INTERACT = registerInternal("block_interact", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BLOCK, ContextTypes.ENTITY, ContextTypes.ITEM), BlockInteractPower.CODEC, BlockInteractPower.PACKET_CODEC);
	public static final PowerType<CallbackPower> CALLBACK = registerInternal("callback", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), CallbackPower.CODEC, CallbackPower.PACKET_CODEC);
	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<ModifyInvisibilityPower> MODIFY_INVISIBILITY = registerInternal("modify/invisibility", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyInvisibilityPower.CODEC, ModifyInvisibilityPower.PACKET_CODEC);
	public static final PowerType<ModifyModelColorOtherPower> MODIFY_MODEL_COLOR_OTHER = registerInternal("modify/model_color/other", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyModelColorOtherPower.CODEC, ModifyModelColorOtherPower.PACKET_CODEC);
	public static final PowerType<ModifyModelColorSelfPower> MODIFY_MODEL_COLOR_SELF = registerInternal("modify/model_color/self", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY), ModifyModelColorSelfPower.CODEC, ModifyModelColorSelfPower.PACKET_CODEC);
	public static final PowerType<ModifyShakingPower> MODIFY_SHAKING = registerInternal("modify/shaking", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), ModifyShakingPower.CODEC, ModifyShakingPower.PACKET_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), MultiplePower.CODEC, MultiplePower.PACKET_CODEC);
	public static final PowerType<TickingPower> TICKING = registerInternal("ticking", ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.ENTITY), TickingPower.CODEC, TickingPower.PACKET_CODEC);

	public static void registerAll() {
		ALIASES.addPathAlias("invisibility", getId(MODIFY_INVISIBILITY).getPath());
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
