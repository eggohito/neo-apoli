package io.github.eggohito.neo_apoli.key.manager;

import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.key.KeyState;
import io.github.eggohito.neo_apoli.network.packet.serverbound.ServerboundUpdateKeyStatesPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public class ServerKeyStateManager implements KeyStateManager {

	protected final ThreadLocal<Map<UUID, Map<String, KeyState>>> previousStates = ThreadLocal.withInitial(Object2ObjectLinkedOpenHashMap::new);
	protected final ThreadLocal<Map<UUID, Map<String, KeyState>>> currentStates = ThreadLocal.withInitial(Object2ObjectLinkedOpenHashMap::new);

	public ServerKeyStateManager() {

		if (INSTANCE != null) {
			throw new IllegalStateException("Key state manager is already initialized!");
		}

	}

	@Override
	public Optional<KeyState> getState(UUID uuid, String key) {
		return Optional
			.ofNullable(currentStates.get().get(uuid))
			.flatMap(states -> Optional.ofNullable(states.get(key)));
	}

	@Override
	public void init() {

		ServerPlayNetworking.registerGlobalReceiver(ServerboundUpdateKeyStatesPacket.TYPE, this::receive);

		ServerTickEvents.END_SERVER_TICK.register(ID, this::tick);
		ServerPlayConnectionEvents.DISCONNECT.register(ID, (handler, server) -> this.disconnect(handler));

	}

	private void disconnect(ServerGamePacketListenerImpl handler) {

		UUID uuid = handler.player.getUUID();

		previousStates.get().remove(uuid);
		currentStates.get().remove(uuid);

	}

	private void receive(ServerboundUpdateKeyStatesPacket payload, ServerPlayNetworking.Context context) {

		ServerPlayer player = context.player();
		ServerLevel level = player.serverLevel();

		UUID uuid = player.getUUID();

		Map<String, KeyState> previous = previousStates.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());
		Map<String, KeyState> current = currentStates.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());

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
					KeyStateEvents.PRESSED.invoker().onPress(player, previousState, currentState);
				}

				else {
					KeyStateEvents.RELEASED.invoker().onRelease(player, previousState, currentState);
				}

			}

		}

	}

	private void tick(MinecraftServer server) {

		for (var player : server.getPlayerList().getPlayers()) {

			UUID uuid = player.getUUID();

			Map<String, KeyState> previous = previousStates.get().getOrDefault(uuid, new Object2ObjectOpenHashMap<>());
			Map<String, KeyState> current = currentStates.get().getOrDefault(uuid, new Object2ObjectOpenHashMap<>());

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

}
