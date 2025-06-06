package io.github.eggohito.neo_apoli.networking.packet;

import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(SynchronizePowersS2CPacket.ID, SynchronizePowersS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerTagsS2CPacket.ID, SynchronizePowerTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeConditionsS2CPacket.ID, SynchronizeConditionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeActionsS2CPacket.ID, SynchronizeActionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeActionTagsS2CPacket.ID, SynchronizeActionTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeDataCommandStorageS2CPacket.ID, SynchronizeDataCommandStorageS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(RequestPowerTagsC2SPacket.ID, RequestPowerTagsC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestActionTagsC2SPacket.ID, RequestActionTagsC2SPacket.CODEC);

	}

}
