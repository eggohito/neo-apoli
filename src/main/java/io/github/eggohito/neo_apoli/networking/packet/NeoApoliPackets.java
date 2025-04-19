package io.github.eggohito.neo_apoli.networking.packet;

import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeDataCommandStorageS2CPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowersS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(SynchronizePowersS2CPacket.ID, SynchronizePowersS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeDataCommandStorageS2CPacket.ID, SynchronizeDataCommandStorageS2CPacket.CODEC);

	}

}
