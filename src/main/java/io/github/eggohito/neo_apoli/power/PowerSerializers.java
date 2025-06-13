package io.github.eggohito.neo_apoli.power;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class PowerSerializers {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<Power.Serializer<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.POWER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, Power.Serializer<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE);

	public static final DummyPower.Serializer DUMMY = registerInternal("dummy", new DummyPower.Serializer());
	public static final MultiplePower.Serializer MULTIPLE = register(MultiplePower.ID, new MultiplePower.Serializer());

	public static final CallbackPower.Serializer CALLBACK = registerInternal("callback", new CallbackPower.Serializer());
	public static final GiveItemsPower.Serializer GIVE_ITEMS = registerInternal("give_items", new GiveItemsPower.Serializer());
	public static final HarvestableBlockPower.Serializer HARVESTABLE_BLOCK = registerInternal("harvestable_block", new HarvestableBlockPower.Serializer());
	public static final OnBlockBreakPower.Serializer ON_BLOCK_BREAK = registerInternal("on_block_break", new OnBlockBreakPower.Serializer());
	public static final TickingPower.Serializer TICKING = registerInternal("ticking", new TickingPower.Serializer());

	public static void registerAll() {

	}

	private static <P extends Power, S extends Power.Serializer<P>> S registerInternal(String path, S serializer) {
		return register(NeoApoli.id(path), serializer);
	}

	public static <P extends Power, S extends Power.Serializer<P>> S register(Identifier id, S serializer) {
		return Registry.register(NeoApoliRegistries.POWER_TYPE, id, serializer);
	}

}
