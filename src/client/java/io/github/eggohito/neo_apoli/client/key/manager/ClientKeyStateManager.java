package io.github.eggohito.neo_apoli.client.key.manager;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.key.manager.ServerKeyStateManager;
import io.github.eggohito.neo_apoli.network.packet.serverbound.ServerboundUpdateKeyStatesPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.UUID;

@ApiStatus.Internal
public final class ClientKeyStateManager extends ServerKeyStateManager {

	private void disconnect() {
		this.previousStates.remove();
		this.currentStates.remove();
	}

	private void tick(Minecraft client) {

		LocalPlayer player = client.player;
		ClientLevel level = client.level;

		if (player == null || level == null) {
			return;
		}

		UUID uuid = player.getUUID();
		Object2BooleanMap<String> updates = new Object2BooleanOpenHashMap<>();

		Map<String, KeyState> previous = previousStates.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());
		Map<String, KeyState> current = currentStates.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());

		for (var keyMapping : client.options.keyMappings) {

			String id = keyMapping.getName();
			boolean pressed = keyMapping.isDown();

			KeyState previousState = previous.computeIfAbsent(id, k -> new KeyState(id, 0));
			KeyState currentState = previousState;

			boolean changed = previousState.pressed() != pressed;

			if (changed) {

				long pressedTime = pressed ? level.getGameTime() : 0;
				currentState = new KeyState(id, pressedTime);

				updates.put(id, pressed);

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
		send(updates);

	}

	private void send(Object2BooleanMap<String> updates) {

		if (!updates.isEmpty()) {
			ClientPlayNetworking.send(new ServerboundUpdateKeyStatesPacket(updates));
		}

	}

	public static void init() {

		if (!(INSTANCE instanceof ClientKeyStateManager clientStates)) {
			throw new IllegalStateException("Expected '" + ClientKeyStateManager.class.getName() + "', got '" + INSTANCE.getClass().getName() + "'");
		}

		ClientTickEvents.END_CLIENT_TICK.register(ID, clientStates::tick);
		ClientPlayConnectionEvents.DISCONNECT.register(ID, (handler, client) -> clientStates.disconnect());

	}

}
