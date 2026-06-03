package io.github.eggohito.neo_apoli.client.condition.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.condition.manager.ConditionManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class ClientConditionManager extends ConditionManager {

	private static final Map<ResourceLocation, Tag> COLLECTED_CONDITIONS = new Object2ObjectLinkedOpenHashMap<>();

	private ClientConditionManager(Void ignored) {

	}

	public static void init() {

	}

	static {

		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateConditionsPacket.TYPE, (payload, context) -> {
			COLLECTED_CONDITIONS.putAll(payload.conditions());
			context.responseSender().sendPacket(ServerboundSyncAcknowledgedPacket.INSTANCE);
		});

		ClientPlayConnectionEvents.INIT.register(ID, (handler, client) -> {

			ClientPlayNetworking.registerReceiver(ClientboundUpdateConditionsPacket.TYPE, (payload, context) -> payload.handle(handler.registryAccess()));
			ConditionManager.conditions = unpackConditions(handler.registryAccess(), COLLECTED_CONDITIONS);

			COLLECTED_CONDITIONS.clear();

		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> conditions = ImmutableMap.of());

	}

}
