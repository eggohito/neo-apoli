package io.github.eggohito.neo_apoli.impl.log;

import io.github.eggohito.neo_apoli.network.packet.s2c.ClearLogsS2CPacket;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.Set;

public class NeoApoliLoggerImpl {

	public static final Set<String> CACHE = new ObjectOpenHashSet<>();

	public static void init() {

		PayloadTypeRegistry.playS2C().register(ClearLogsS2CPacket.TYPE, ClearLogsS2CPacket.CODEC);

		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> {

			if (!success) {
				return;
			}

			CACHE.clear();
			server.getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, ClearLogsS2CPacket.INSTANCE));

		});

	}

}
