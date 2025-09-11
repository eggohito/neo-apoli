package io.github.eggohito.neo_apoli.networking.packet;

import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.c2s.TriggerPowerImplsC2SPacket;
import io.github.eggohito.neo_apoli.networking.packet.s2c.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(ClearLogsS2CPacket.ID, ClearLogsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(DismountEntityS2CPacket.ID, DismountEntityS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(MountEntityS2CPacket.ID, MountEntityS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeActionsS2CPacket.ID, SynchronizeActionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeActionTagsS2CPacket.ID, SynchronizeActionTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeConditionsS2CPacket.ID, SynchronizeConditionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeDataCommandStorageS2CPacket.ID, SynchronizeDataCommandStorageS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeEntityTypeTagCacheS2CPacket.ID, SynchronizeEntityTypeTagCacheS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerDataS2CPacket.ID, SynchronizePowerDataS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerRecipeDisplaysS2CPacket.ID, SynchronizePowerRecipeDisplaysS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowersS2CPacket.ID, SynchronizePowersS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerTagsS2CPacket.ID, SynchronizePowerTagsS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(RequestPowerTagsC2SPacket.ID, RequestPowerTagsC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestActionTagsC2SPacket.ID, RequestActionTagsC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(TriggerPowerImplsC2SPacket.ID, TriggerPowerImplsC2SPacket.CODEC);

	}

}
