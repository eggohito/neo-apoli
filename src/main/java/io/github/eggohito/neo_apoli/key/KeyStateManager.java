package io.github.eggohito.neo_apoli.key;

import io.github.eggohito.neo_apoli.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.network.packet.c2s.SynchronizeKeyStatesC2SPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class KeyStateManager {

	private static final Map<UUID, Map<String, KeyState>> STATES = new Object2ObjectOpenHashMap<>();

	private KeyStateManager() {

	}

	@ApiStatus.Internal
	@Environment(EnvType.CLIENT)
	public static void startTrackingClient(Minecraft client) {

		LocalPlayer player = client.player;
		ClientLevel world = client.level;

		if (player == null || world == null) {
			return;
		}

		Map<String, KeyState> states = STATES.computeIfAbsent(player.getUUID(), k -> new Object2ObjectOpenHashMap<>());
		Object2BooleanMap<String> updatedStates = new Object2BooleanOpenHashMap<>();

		for (var keyMapping : client.options.keyMappings) {

			String id = keyMapping.getName();
			boolean pressed = keyMapping.isDown();

			KeyState state = states.get(id);
			boolean changed = state == null || pressed != state.pressed();

			if (changed) {

				long pressedTime = pressed ? world.getGameTime() : 0;
				state = new KeyState(id, pressedTime);

				updatedStates.put(id, pressed);

			}

			states.put(id, state);

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

		if (!updatedStates.isEmpty()) {
			ClientPlayNetworking.send(new SynchronizeKeyStatesC2SPacket(updatedStates));
		}

	}

	@ApiStatus.Internal
	@Environment(EnvType.CLIENT)
	public static void stopTrackingClient(ClientPacketListener ignoredHandler, Minecraft ignoredClient) {
		STATES.clear();
	}

	@ApiStatus.Internal
	public static void startTrackingServer(MinecraftServer server) {

		for (var player : server.getPlayerList().getPlayers()) {

			Map<String, KeyState> states = STATES.getOrDefault(player.getUUID(), new Object2ObjectOpenHashMap<>());

			for (var state : states.values()) {

				if (state.pressed()) {
					KeyStateEvents.HELD.invoker().run(player, state);
				}

			}

		}

	}

	@ApiStatus.Internal
	public static void stopTrackingServer(ServerGamePacketListenerImpl handler, MinecraftServer ignoredServer) {
		STATES.remove(handler.player.getUUID());
	}

	@ApiStatus.Internal
	public static void updateStates(SynchronizeKeyStatesC2SPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer player = context.player();
		ServerLevel world = player.serverLevel();

		for (var entry : payload.states().object2BooleanEntrySet()) {

			String id = entry.getKey();
			boolean pressed = entry.getBooleanValue();

			Map<String, KeyState> states = STATES.computeIfAbsent(player.getUUID(), k -> new Object2ObjectOpenHashMap<>());
			KeyState state = states.get(id);

			if (state == null || pressed != state.pressed()) {

				long pressedTime = pressed ? world.getGameTime() : 0;
				state = new KeyState(id, pressedTime);

				states.put(id, state);

				if (pressed) {
					KeyStateEvents.PRESSED.invoker().run(player, state);
				}

				else {
					KeyStateEvents.RELEASED.invoker().run(player, state);
				}

			}

		}

	}

	public static Optional<KeyState> getState(UUID entity, String id) {
		return Optional.ofNullable(STATES.get(entity)).flatMap(map -> Optional.ofNullable(map.get(id)));
	}

}
