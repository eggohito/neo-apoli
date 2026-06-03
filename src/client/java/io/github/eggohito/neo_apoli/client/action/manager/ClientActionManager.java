package io.github.eggohito.neo_apoli.client.action.manager;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public final class ClientActionManager extends ActionManager {

	private static final Map<ResourceLocation, Tag> COLLECTED_ACTIONS = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<ResourceLocation, List<ResourceLocation>> COLLECTED_TAGS = new Object2ObjectLinkedOpenHashMap<>();

	private ClientActionManager(Void ignored) {

	}

	public static void init() {

	}

	static {

		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundSyncInitiatedPacket.TYPE, (payload, context) -> payload.handle(context.responseSender()));
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateActionTagsPacket.TYPE, (payload, context) -> COLLECTED_TAGS.putAll(payload.tags()));
		ClientConfigurationNetworking.registerGlobalReceiver(ClientboundUpdateActionsPacket.TYPE, (payload, context) -> COLLECTED_ACTIONS.putAll(payload.actions()));

		ClientPlayConnectionEvents.INIT.register(ID, (handler, client) -> {

			ClientPlayNetworking.registerReceiver(ClientboundUpdateActionsPacket.TYPE, (payload, context) -> payload.handle(handler.registryAccess()));
			ClientPlayNetworking.registerReceiver(ClientboundUpdateActionTagsPacket.TYPE, (payload, context) -> payload.handle());

			ActionManager.actions = unpackActions(handler.registryAccess(), COLLECTED_ACTIONS);
			ActionManager.tags = unpackTags(COLLECTED_TAGS);

			COLLECTED_ACTIONS.clear();
			COLLECTED_TAGS.clear();

		});

		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> {
			actions = ImmutableMap.of();
			tags = ImmutableMap.of();
		});

	}

}
