package io.github.eggohito.neo_apoli.client.impl.key;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import io.github.eggohito.neo_apoli.network.packet.c2s.SynchronizeKeyStatesC2SPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

import java.util.Map;
import java.util.UUID;

public class KeyStateClientManagerImpl extends KeyStateManagerImpl {

	private static void clientTick(Minecraft client) {

		LocalPlayer player = client.player;
		ClientLevel level = client.level;

		if (player == null || level == null) {
			return;
		}

		UUID uuid = player.getUUID();
		Object2BooleanMap<String> updated = new Object2BooleanOpenHashMap<>();

		Map<String, KeyState> previous = PREVIOUS_STATES.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());
		Map<String, KeyState> current = CURRENT_STATES.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());

		for (var keyMapping : client.options.keyMappings) {

			String id = keyMapping.getName();
			boolean pressed = keyMapping.isDown();

			KeyState previousState = previous.computeIfAbsent(id, k -> new KeyState(id, 0));
			KeyState currentState = previousState;

			boolean changed = previousState.pressed() != pressed;

			if (changed) {

				long pressedTime = pressed ? level.getGameTime() : 0;
				currentState = new KeyState(id, pressedTime);

				updated.put(id, pressed);

			}

			current.put(id, currentState);

			if (changed) {

				if (pressed) {
					KeyStateEvents.PRESSED.invoker().onPress(player, previousState, currentState);
				}

				else {
					KeyStateEvents.RELEASED.invoker().onRelease(player, previousState, currentState);
				}

			}

			if (currentState.pressed()) {
				KeyStateEvents.HELD.invoker().onHold(player, previousState, currentState);
			}

		}

		previous.putAll(current);
		send(updated);

	}

	private static void clientDisconnect() {
		PREVIOUS_STATES.get().clear();
		CURRENT_STATES.get().clear();
	}

	private static void send(Object2BooleanMap<String> updated) {

		if (!updated.isEmpty()) {
			ClientPlayNetworking.send(new SynchronizeKeyStatesC2SPacket(updated));
		}

	}

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(KeyStateClientManagerImpl::clientTick);
		ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> clientDisconnect());
	}

}
