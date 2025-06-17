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

	public static final PowerType<BlockBreakPower> BLOCK_BREAK = registerInternal("block_break", ContextTypes.BLOCK, BlockBreakPower.CODEC, BlockBreakPower.PACKET_CODEC);
	public static final PowerType<BlockInteractPower> BLOCK_INTERACT = registerInternal("block_interact", ContextTypes.BLOCK, BlockInteractPower.CODEC, BlockInteractPower.PACKET_CODEC);
	public static final PowerType<CallbackPower> CALLBACK = registerInternal("callback", ContextTypes.GENERIC, CallbackPower.CODEC, CallbackPower.PACKET_CODEC);
	public static final PowerType<DummyPower> DUMMY = registerInternal("dummy", ContextTypes.GENERIC, DummyPower.CODEC, DummyPower.PACKET_CODEC);
	public static final PowerType<GiveItemsPower> GIVE_ITEMS = registerInternal("give_items", ContextTypes.GENERIC, GiveItemsPower.CODEC, GiveItemsPower.PACKET_CODEC);
	public static final PowerType<HarvestableBlockPower> HARVESTABLE_BLOCK = registerInternal("harvestable_block", ContextTypes.BLOCK, HarvestableBlockPower.CODEC, HarvestableBlockPower.PACKET_CODEC);
	public static final PowerType<MultiplePower> MULTIPLE = register(MultiplePower.ID, ContextTypes.GENERIC, MultiplePower.CODEC, MultiplePower.PACKET_CODEC);
	public static final PowerType<TickingPower> TICKING = registerInternal("ticking", ContextTypes.GENERIC, TickingPower.CODEC, TickingPower.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends Power> PowerType<P> registerInternal(String path, ContextType contextType, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), contextType, mapCodec, packetCodec);
	}

	public static <P extends Power> PowerType<P> register(Identifier id, ContextType contextType, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, new PowerType<>(contextType, mapCodec, packetCodec));
	}

}
