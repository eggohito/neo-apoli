package io.github.eggohito.neo_apoli.client.power.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public final class ClientPowerManager extends PowerManager {

	private static final Map<PowerIdentifier, Tag> COLLECTED_POWERS = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<ResourceLocation, List<PowerIdentifier>> COLLECTED_TAGS = new Object2ObjectLinkedOpenHashMap<>();

	private ClientPowerManager() {

	}

	public static void init() {

	}

	static {

		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundSyncInitiatedPacket.TYPE, (payload, context) -> payload.handle(context.responseSender()));
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdatePowerTagsPacket.TYPE, (payload, context) -> COLLECTED_TAGS.putAll(payload.tags()));
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdatePowersPacket.TYPE, (payload, context) -> COLLECTED_POWERS.putAll(payload.powers()));

		ClientPlayConnectionEvents.INIT.register(ID, (handler, client) -> {

			ClientPlayNetworking.registerReceiver(ClientboundUpdatePowersPacket.TYPE, (payload, context) -> payload.handle(handler.registryAccess()));
			ClientPlayNetworking.registerReceiver(ClientboundUpdatePowerTagsPacket.TYPE, (payload, context) -> payload.handle());

			PowerManager.powers = unpackPowers(handler.registryAccess(), COLLECTED_POWERS);
			PowerManager.tags = unpackTags(COLLECTED_TAGS);

			COLLECTED_POWERS.clear();
			COLLECTED_TAGS.clear();

		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			powers = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}
