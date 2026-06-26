package io.github.eggohito.neo_apoli.network.packet;

import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import io.github.eggohito.neo_apoli.impl.power.PowersBuilderImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundCommandStorageUpdatePacket;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundPowerDataUpdatePacket;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundPowerRecipeDisplaysUpdatePacket;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class NeoApoliPackets {

	public static void registerAll() {

		PayloadTypeRegistry.configurationC2S().register(PowerManager.ServerboundSyncAcknowledgedPacket.TYPE, PowerManager.ServerboundSyncAcknowledgedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(PowerManager.ClientboundSyncInitiatedPacket.TYPE, PowerManager.ClientboundSyncInitiatedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(PowerManager.ClientboundUpdatePowerTagsPacket.TYPE, PowerManager.ClientboundUpdatePowerTagsPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(PowerManager.ClientboundUpdatePowersPacket.TYPE, PowerManager.ClientboundUpdatePowersPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.ClientboundUpdatePowerTagsPacket.TYPE, PowerManager.ClientboundUpdatePowerTagsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowerManager.ClientboundUpdatePowersPacket.TYPE, PowerManager.ClientboundUpdatePowersPacket.CODEC);

		PayloadTypeRegistry.configurationC2S().register(ActionManager.ServerboundSyncAcknowledgedPacket.TYPE, ActionManager.ServerboundSyncAcknowledgedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ActionManager.ClientboundSyncInitiatedPacket.TYPE, ActionManager.ClientboundSyncInitiatedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ActionManager.ClientboundUpdateActionTagsPacket.TYPE, ActionManager.ClientboundUpdateActionTagsPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ActionManager.ClientboundUpdateActionsPacket.TYPE, ActionManager.ClientboundUpdateActionsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.ClientboundUpdateActionTagsPacket.TYPE, ActionManager.ClientboundUpdateActionTagsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ActionManager.ClientboundUpdateActionsPacket.TYPE, ActionManager.ClientboundUpdateActionsPacket.CODEC);

		PayloadTypeRegistry.configurationC2S().register(ConditionManager.ServerboundSyncAcknowledgedPacket.TYPE, ConditionManager.ServerboundSyncAcknowledgedPacket.CODEC);
		PayloadTypeRegistry.configurationS2C().register(ConditionManager.ClientboundUpdateConditionsPacket.TYPE, ConditionManager.ClientboundUpdateConditionsPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ConditionManager.ClientboundUpdateConditionsPacket.TYPE, ConditionManager.ClientboundUpdateConditionsPacket.CODEC);

		PayloadTypeRegistry.playS2C().register(ClientboundCommandStorageUpdatePacket.TYPE, ClientboundCommandStorageUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerDataUpdatePacket.TYPE, ClientboundPowerDataUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, ClientboundPowerRecipeDisplaysUpdatePacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersBuilderImpl.ClientboundGrantPowersPacket.TYPE, PowersBuilderImpl.ClientboundGrantPowersPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(PowersBuilderImpl.ClientboundRevokePowersPacket.TYPE, PowersBuilderImpl.ClientboundRevokePowersPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.TYPE, KeyStateManagerImpl.ServerboundKeyStatesUpdatePacket.CODEC);

	}

}
