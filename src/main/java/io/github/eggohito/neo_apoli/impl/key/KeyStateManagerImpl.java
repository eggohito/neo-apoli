package io.github.eggohito.neo_apoli.impl.key;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.network.packet.c2s.SynchronizeKeyStatesC2SPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Map;
import java.util.UUID;

public class KeyStateManagerImpl {

	public static final ThreadLocal<Map<UUID, Map<String, KeyState>>> PREVIOUS_STATES = ThreadLocal.withInitial(Object2ObjectOpenHashMap::new);
	public static final ThreadLocal<Map<UUID, Map<String, KeyState>>> CURRENT_STATES = ThreadLocal.withInitial(Object2ObjectOpenHashMap::new);

	private static void serverTick(MinecraftServer server) {

		for (var player : server.getPlayerList().getPlayers()) {

			UUID uuid = player.getUUID();

			Map<String, KeyState> previous = PREVIOUS_STATES.get().getOrDefault(uuid, new Object2ObjectOpenHashMap<>());
			Map<String, KeyState> current = CURRENT_STATES.get().getOrDefault(uuid, new Object2ObjectOpenHashMap<>());

			for (var currentState : current.values()) {

				String id = currentState.id();
				KeyState previousState = previous.computeIfAbsent(id, k -> new KeyState(id, 0));

				if (currentState.pressed()) {
					KeyStateEvents.HELD.invoker().onHold(player, previousState, currentState);
				}

			}

			previous.putAll(current);

		}

	}

	private static void serverDisconnect(ServerGamePacketListenerImpl listener) {

		UUID uuid = listener.getPlayer().getUUID();

		PREVIOUS_STATES.get().remove(uuid);
		CURRENT_STATES.get().remove(uuid);

	}

	private static void receive(SynchronizeKeyStatesC2SPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer recipient = context.player();
		ServerLevel level = recipient.serverLevel();

		UUID uuid = recipient.getUUID();

		Map<String, KeyState> previous = PREVIOUS_STATES.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());
		Map<String, KeyState> current = CURRENT_STATES.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());

		for (var entry : payload.states().object2BooleanEntrySet()) {

			String id = entry.getKey();
			boolean pressed = entry.getBooleanValue();

			KeyState previousState = previous.computeIfAbsent(id, k -> new KeyState(id, 0));
			KeyState currentState;

			if (previousState.pressed() != pressed) {

				long pressedTime = pressed ? level.getGameTime() : 0;
				currentState = new KeyState(id, pressedTime);

				current.put(id, currentState);

				if (pressed) {
					KeyStateEvents.PRESSED.invoker().onPress(recipient, previousState, currentState);
				}

				else {
					KeyStateEvents.RELEASED.invoker().onRelease(recipient, previousState, currentState);
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
