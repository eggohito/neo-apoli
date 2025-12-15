package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.network.packet.s2c.ClearLogsS2CPacket;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.Set;

public class NeoApoliLogger {

	public static final Logger INSTANCE = LoggerFactory.getLogger(NeoApoli.MOD_NAMESPACE);
	private static final Set<String> LOGS = new ObjectOpenHashSet<>();

	public static void logOnce(Level level, String message) {

		if (LOGS.add(message)) {
			INSTANCE.atLevel(level).log(message);
		}

	}

	public static void onReloadServerBound(MinecraftServer server, ResourceManager ignoredManager) {
		LOGS.clear();
		server.getPlayerList().getPlayers().forEach(player -> ServerPlayNetworking.send(player, ClearLogsS2CPacket.INSTANCE));
	}

	@Environment(EnvType.CLIENT)
	public static void onReloadClientBound(ClearLogsS2CPacket ignoredPayload, ClientPlayNetworking.Context ignoreddContext) {
		LOGS.clear();
	}

}
