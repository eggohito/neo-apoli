package io.github.eggohito.neo_apoli.client.impl.key;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.impl.key.KeyStateManagerImpl;
import io.github.eggohito.neo_apoli.network.packet.c2s.SynchronizeKeyStatesC2SPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;

import java.util.Map;

public class KeyStateClientManagerImpl extends KeyStateManagerImpl {

	private static void clientTick(Minecraft client) {

		LocalPlayer player = client.player;
		ClientLevel level = client.level;

		if (player == null || level == null) {
			return;
		}

		Map<String, KeyState> current = STATES.computeIfAbsent(player.getUUID(), k -> new Object2ObjectOpenHashMap<>());
		Object2BooleanMap<String> updated = new Object2BooleanOpenHashMap<>();

		for (var keyMapping : client.options.keyMappings) {

			String id = keyMapping.getName();
			boolean pressed = keyMapping.isDown();

			KeyState state = current.get(id);
			boolean changed = state == null || state.pressed() != pressed;

			if (changed) {

				long pressedTime = pressed ? level.getGameTime() : 0;
				state = new KeyState(id, pressedTime);

				updated.put(id, pressed);

			}

			current.put(id, state);

			if (changed) {

				if (pressed) {
					KeyStateEvents.PRESSED.invoker().run(player, state);
				}

				else {
					KeyStateEvents.RELEASED.invoker().run(player, state);
				}

			}

			if (state.pressed()) {
				KeyStateEvents.HELD.invoker().run(player, state);
			}

		}

		send(updated);

	}

	private static void clientDisconnect() {
		STATES.clear();
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
