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

		PayloadTypeRegistry.configurationC2S().register(PowerManager.ServerboundSyncAcknowledgedPacket.TYPE, PowerManager.ServerboundSyncAcknowledgedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(PowerManager.ClientboundSyncInitiatedPacket.TYPE, PowerManager.ClientboundSyncInitiatedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(PowerManager.ClientboundUpdatePowersPacket.TYPE, PowerManager.ClientboundUpdatePowersPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(PowerManager.ClientboundUpdateTagsPacket.TYPE, PowerManager.ClientboundUpdateTagsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.ClientboundUpdatePowersPacket.TYPE, PowerManager.ClientboundUpdatePowersPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.ClientboundUpdateTagsPacket.TYPE, PowerManager.ClientboundUpdateTagsPacket.CODEC);

		PayloadTypeRegistry.configurationC2S().register(ActionManager.ServerboundSyncAcknowledgedPacket.TYPE, ActionManager.ServerboundSyncAcknowledgedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ActionManager.ClientboundSyncInitiatedPacket.TYPE, ActionManager.ClientboundSyncInitiatedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ActionManager.ClientboundUpdateActionsPacket.TYPE, ActionManager.ClientboundUpdateActionsPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ActionManager.ClientboundUpdateTagsPacket.TYPE, ActionManager.ClientboundUpdateTagsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.ClientboundUpdateActionsPacket.TYPE, ActionManager.ClientboundUpdateActionsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.ClientboundUpdateTagsPacket.TYPE, ActionManager.ClientboundUpdateTagsPacket.CODEC);

		PayloadTypeRegistry.configurationC2S().register(ConditionManager.ServerboundSyncAcknowledgedPacket.TYPE, ConditionManager.ServerboundSyncAcknowledgedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ConditionManager.ClientboundUpdateConditionsPacket.TYPE, ConditionManager.ClientboundUpdateConditionsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ConditionManager.ClientboundUpdateConditionsPacket.TYPE, ConditionManager.ClientboundUpdateConditionsPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(ClientboundCommandStorageUpdatePacket.TYPE, ClientboundCommandStorageUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundDismountEntityPacket.TYPE, ClientboundDismountEntityPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundMountEntityPacket.TYPE, ClientboundMountEntityPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerDataUpdatePacket.TYPE, ClientboundPowerDataUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, ClientboundPowerRecipeDisplaysUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.GrantS2CPacket.TYPE, PowersImpl.GrantS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersImpl.RevokeS2CPacket.TYPE, PowersImpl.RevokeS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.TYPE, KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.CODEC);

	}

}
