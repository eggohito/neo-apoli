package io.github.eggohito.neo_apoli.network.packet;

import io.github.eggohito.neo_apoli.impl.power.PowersBuilderImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.*;
import io.github.eggohito.neo_apoli.network.packet.serverbound.ServerboundUpdateKeyStatesPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(ClientboundUpdateActionsPacket.TYPE, ClientboundUpdateActionsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundUpdateConditionsPacket.TYPE, ClientboundUpdateConditionsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundUpdatePowersPacket.TYPE,  ClientboundUpdatePowersPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(ClientboundClearCachedLogsPacket.TYPE, ClientboundClearCachedLogsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundCommandStorageUpdatePacket.TYPE, ClientboundCommandStorageUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerDataUpdatePacket.TYPE, ClientboundPowerDataUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, ClientboundPowerRecipeDisplaysUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundUpdateNestedTagPacket.TYPE, ClientboundUpdateNestedTagPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersBuilderImpl.ClientboundGrantPowersPacket.TYPE, PowersBuilderImpl.ClientboundGrantPowersPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersBuilderImpl.ClientboundRevokePowersPacket.TYPE, PowersBuilderImpl.ClientboundRevokePowersPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(ServerboundUpdateKeyStatesPacket.TYPE, ServerboundUpdateKeyStatesPacket.CODEC);

	}

}
