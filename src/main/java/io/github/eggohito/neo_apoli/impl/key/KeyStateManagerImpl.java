package io.github.eggohito.neo_apoli.impl.key;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.network.packet.c2s.SynchronizeKeyStatesC2SPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class KeyStateManagerImpl {

	public static final Map<UUID, Map<String, KeyState>> STATES = new Object2ObjectOpenHashMap<>();

	private static void serverTick(MinecraftServer server) {

		for (var player : server.getPlayerList().getPlayers()) {

			Collection<KeyState> states = STATES
				.getOrDefault(player.getUUID(), new Object2ObjectOpenHashMap<>())
				.values();

			for (var state : states) {

				if (state.pressed()) {
					KeyStateEvents.HELD.invoker().run(player, state);
				}

			}

		}

	}

	private static void serverDisconnect(ServerGamePacketListenerImpl listener) {
		STATES.remove(listener.getPlayer().getUUID());
	}

	private static void receive(SynchronizeKeyStatesC2SPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer recipient = context.player();
		ServerLevel level = recipient.serverLevel();

		for (var entry : payload.states().object2BooleanEntrySet()) {

			String id = entry.getKey();
			boolean pressed = entry.getBooleanValue();

			Map<String, KeyState> current = STATES.computeIfAbsent(recipient.getUUID(), k -> new Object2ObjectOpenHashMap<>());
			KeyState state = current.get(id);

			if (state == null || state.pressed() != pressed) {

				long pressedTime = pressed ? level.getGameTime() : 0;
				state = new KeyState(id, pressedTime);

				current.put(id, state);

				if (pressed) {
					KeyStateEvents.PRESSED.invoker().run(recipient, state);
				}

				else {
					KeyStateEvents.RELEASED.invoker().run(recipient, state);
				}

			}

		}

	}

	public static void init() {

		ServerTickEvents.END_SERVER_TICK.register(KeyStateManagerImpl::serverTick);
		ServerPlayConnectionEvents.DISCONNECT.register((listener, server) -> serverDisconnect(listener));

		PayloadTypeRegistry.playC2S().register(SynchronizeKeyStatesC2SPacket.TYPE, SynchronizeKeyStatesC2SPacket.CODEC);

		ServerPlayConnectionEvents.INIT.register((listener, server) ->
			ServerPlayNetworking.registerReceiver(listener, SynchronizeKeyStatesC2SPacket.TYPE, KeyStateManagerImpl::receive)
		);

	}

}
