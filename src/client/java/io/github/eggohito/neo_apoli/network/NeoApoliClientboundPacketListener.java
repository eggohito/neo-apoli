package io.github.eggohito.neo_apoli.network;

import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.impl.power.PowersBuilderImpl;
import io.github.eggohito.neo_apoli.network.packet.clientbound.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public final class NeoApoliClientboundPacketListener {

	public static void init() {

		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ClientPlayNetworking.registerReceiver(ClientboundDismountEntityPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ClientboundMountEntityPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ClientboundCommandStorageUpdatePacket.TYPE, (payload, context) -> payload.handle((CommandStorageHolder) context.client()));
			ClientPlayNetworking.registerReceiver(ClientboundPowerDataUpdatePacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(ClientboundPowerRecipeDisplaysUpdatePacket.TYPE, (payload, context) -> payload.handle((PowerRecipeDisplayHolder) context.client()));
			ClientPlayNetworking.registerReceiver(PowersBuilderImpl.ClientboundGrantPowersPacket.TYPE, (payload, context) -> payload.handle(context.player().level()));
			ClientPlayNetworking.registerReceiver(PowersBuilderImpl.ClientboundRevokePowersPacket.TYPE,  (payload, context) -> payload.handle(context.player().level()));
		});

	}

}
