package io.github.eggohito.neo_apoli.network.packet;

import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.condition.ConditionManager;
import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestActionTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.c2s.RequestPowerTagsC2SPacket;
import io.github.eggohito.neo_apoli.network.packet.s2c.*;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(DismountEntityS2CPacket.TYPE, DismountEntityS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(MountEntityS2CPacket.TYPE, MountEntityS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.SynchronizeS2CPacket.TYPE, ActionManager.SynchronizeS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.SynchronizeTagsS2CPacket.TYPE, ActionManager.SynchronizeTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ConditionManager.SynchronizeConditionsS2CPacket.TYPE, ConditionManager.SynchronizeConditionsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizeCommandStorageS2CPacket.TYPE, SynchronizeCommandStorageS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerDataS2CPacket.TYPE, SynchronizePowerDataS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SynchronizePowerRecipeDisplaysS2CPacket.TYPE, SynchronizePowerRecipeDisplaysS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.SynchronizeS2CPacket.TYPE, PowerManager.SynchronizeS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.SynchronizeTagsS2CPacket.TYPE, PowerManager.SynchronizeTagsS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.GrantS2CPacket.TYPE, PowersImpl.GrantS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.RevokeS2CPacket.TYPE, PowersImpl.RevokeS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(RequestPowerTagsC2SPacket.TYPE, RequestPowerTagsC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(RequestActionTagsC2SPacket.TYPE, RequestActionTagsC2SPacket.CODEC);

	}

}
