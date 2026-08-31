package io.github.eggohito.neo_apoli.network;

import io.github.eggohito.neo_apoli.duck.internal.CommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.internal.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.network.packet.HandshakePacket;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundCommandStorageUpdatePacket;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundPowerDataUpdatePacket;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundPowerRecipeDisplaysUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.NoSuchElementException;

public final class NeoApoliClientboundPacketListener {

	public static void init() {

		ClientConfigurationNetworking.registerGlobalReceiver(HandshakePacket.TYPE, NeoApoliClientboundPacketListener::sendHandshakeReply);

		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ClientboundCommandStorageUpdatePacket.TYPE, (payload, context) -> payload.handle((CommandStorageHolder) context.client()));
			ClientPlayNetworking.registerReceiver(ClientboundPowerDataUpdatePacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, (payload, context) -> payload.handle((PowerRecipeDisplayHolder) context.client()));
		});

	}

	private static void sendHandshakeReply(HandshakePacket payload, ClientConfigurationNetworking.Context context) {

		try {

			String modId = payload.modId();
			ModContainer mod = FabricLoader.getInstance()
				.getModContainer(modId)
				.orElseThrow();

			context.responseSender().sendPacket(new HandshakePacket(modId, mod.getMetadata().getVersion().getFriendlyString()));

		}

		catch (NoSuchElementException ignored) {
			context.responseSender().disconnect(payload.createMissingModComponent());
		}

	}

}
