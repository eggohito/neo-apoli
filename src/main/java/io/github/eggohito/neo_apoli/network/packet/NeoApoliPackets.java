package io.github.eggohito.neo_apoli.network.packet;

import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.s2c.*;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(DismountEntityS2CPacket.TYPE, DismountEntityS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(MountEntityS2CPacket.TYPE, MountEntityS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeActionsS2CPacket.TYPE, SynchronizeActionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeActionTagsS2CPacket.TYPE, SynchronizeActionTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeConditionsS2CPacket.TYPE, SynchronizeConditionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeCommandStorageS2CPacket.TYPE, SynchronizeCommandStorageS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerDataS2CPacket.TYPE, SynchronizePowerDataS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerRecipeDisplaysS2CPacket.TYPE, SynchronizePowerRecipeDisplaysS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowersS2CPacket.TYPE, SynchronizePowersS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerTagsS2CPacket.TYPE, SynchronizePowerTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.GrantS2CPacket.TYPE, PowersImpl.GrantS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.RevokeS2CPacket.TYPE, PowersImpl.RevokeS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(RequestPowerTagsC2SPacket.TYPE, RequestPowerTagsC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestActionTagsC2SPacket.TYPE, RequestActionTagsC2SPacket.CODEC);

	}

}
