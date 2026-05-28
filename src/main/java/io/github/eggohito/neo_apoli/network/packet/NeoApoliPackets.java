package io.github.eggohito.neo_apoli.network.packet;

import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.*;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.playS2C().register(ActionManager.ClientboundActionsUpdatePacket.TYPE, ActionManager.ClientboundActionsUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.ClientboundTagsUpdatePacket.TYPE, ActionManager.ClientboundTagsUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundCommandStorageUpdatePacket.TYPE, ClientboundCommandStorageUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundDismountEntityPacket.TYPE, ClientboundDismountEntityPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundMountEntityPacket.TYPE, ClientboundMountEntityPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerDataUpdatePacket.TYPE, ClientboundPowerDataUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, ClientboundPowerRecipeDisplaysUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ConditionManager.ClientboundConditionsUpdatePacket.TYPE, ConditionManager.ClientboundConditionsUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.ClientboundPowersUpdatePacket.TYPE, PowerManager.ClientboundPowersUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.ClientboundTagsUpdatePacket.TYPE, PowerManager.ClientboundTagsUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.GrantS2CPacket.TYPE, PowersImpl.GrantS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.RevokeS2CPacket.TYPE, PowersImpl.RevokeS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.TYPE, KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.CODEC);

	}

}
