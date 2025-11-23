package io.github.eggohito.neo_apoli.keybinding;

import io.github.eggohito.neo_apoli.integration.KeyBindingEvents;
import io.github.eggohito.neo_apoli.networking.packet.c2s.SynchronizeKeyBindingStatesC2SPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class KeyBindingStateHolder {

	private static final Map<UUID, Map<String, KeyBindingState>> KEYBINDING_STATES = new Object2ObjectOpenHashMap<>();

	private KeyBindingStateHolder() {

	}

	@ApiStatus.Internal
	@Environment(EnvType.CLIENT)
	public static void startTrackingClient(MinecraftClient client) {

		ClientPlayerEntity player = client.player;
		ClientWorld world = client.world;

		if (player == null || world == null) {
			return;
		}

		Map<String, KeyBindingState> states = KEYBINDING_STATES.computeIfAbsent(player.getUuid(), k -> new Object2ObjectOpenHashMap<>());
		Object2BooleanMap<String> statesToUpdate = new Object2BooleanOpenHashMap<>();

		for (var keyBinding : client.options.allKeys) {

			String id = keyBinding.getTranslationKey();
			boolean pressed = keyBinding.isPressed();

			KeyBindingState state = states.get(id);
			boolean changed = state == null || pressed != state.pressed();

			if (changed) {

				long pressedTime = pressed ? world.getTime() : 0;
				state = new KeyBindingState(id, pressedTime);

				statesToUpdate.put(id, pressed);

			}

			states.put(id, state);

			if (changed) {

				if (pressed) {
					KeyBindingEvents.PRESSED.invoker().run(player, state);
				}

				else {
					KeyBindingEvents.RELEASED.invoker().run(player, state);
				}

			}

			if (state.pressed()) {
				KeyBindingEvents.HELD.invoker().run(player, state);
			}

		}

		if (!statesToUpdate.isEmpty()) {
			ClientPlayNetworking.send(new SynchronizeKeyBindingStatesC2SPacket(statesToUpdate));
		}

	}

	@ApiStatus.Internal
	@Environment(EnvType.CLIENT)
	public static void stopTrackingClient(ClientPlayNetworkHandler ignoredHandler, MinecraftClient ignoredClient) {
		KEYBINDING_STATES.clear();
	}

	@ApiStatus.Internal
	public static void startTrackingServer(MinecraftServer server) {

		for (var player : server.getPlayerManager().getPlayerList()) {

			Map<String, KeyBindingState> states = KEYBINDING_STATES.getOrDefault(player.getUuid(), new Object2ObjectOpenHashMap<>());

			for (var state : states.values()) {

				if (state.pressed()) {
					KeyBindingEvents.HELD.invoker().run(player, state);
				}

			}

		}

	}

	@ApiStatus.Internal
	public static void stopTrackingServer(ServerPlayNetworkHandler handler, MinecraftServer ignoredServer) {
		KEYBINDING_STATES.remove(handler.player.getUuid());
	}

	@ApiStatus.Internal
	public static void updateStates(SynchronizeKeyBindingStatesC2SPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayerEntity player = context.player();
		ServerWorld world = player.getServerWorld();

		for (var entry : payload.states().object2BooleanEntrySet()) {

			String id = entry.getKey();
			boolean pressed = entry.getBooleanValue();

			Map<String, KeyBindingState> states = KEYBINDING_STATES.computeIfAbsent(player.getUuid(), k -> new Object2ObjectOpenHashMap<>());
			KeyBindingState state = states.get(id);

			if (state == null || pressed != state.pressed()) {

				long pressedTime = pressed ? world.getTime() : 0;
				state = new KeyBindingState(id, pressedTime);

				states.put(id, state);

				if (pressed) {
					KeyBindingEvents.PRESSED.invoker().run(player, state);
				}

				else {
					KeyBindingEvents.RELEASED.invoker().run(player, state);
				}

			}

		}

	}

	public static Optional<KeyBindingState> getState(UUID entity, String id) {
		return Optional.ofNullable(KEYBINDING_STATES.get(entity)).flatMap(map -> Optional.ofNullable(map.get(id)));
	}

}
