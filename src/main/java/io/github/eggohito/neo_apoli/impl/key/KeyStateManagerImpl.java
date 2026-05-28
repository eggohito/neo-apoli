package io.github.eggohito.neo_apoli.impl.key;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.event.KeyStateEvents;
import io.github.eggohito.neo_apoli.api.key.KeyState;
import io.github.eggohito.neo_apoli.api.key.KeyStateManager;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class KeyStateManagerImpl implements KeyStateManager {

	protected static final ThreadLocal<Map<UUID, Map<String, KeyState>>> PREVIOUS_STATES = ThreadLocal.withInitial(Object2ObjectOpenHashMap::new);
	protected static final ThreadLocal<Map<UUID, Map<String, KeyState>>> CURRENT_STATES = ThreadLocal.withInitial(Object2ObjectOpenHashMap::new);

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

	public static Optional<KeyState> getState(UUID uuid, String key) {
		return Optional.ofNullable(CURRENT_STATES.get().get(uuid)).flatMap(map -> Optional.ofNullable(map.get(key)));
	}

	public static void init() {

		ServerTickEvents.END_SERVER_TICK.register(KeyStateManagerImpl::serverTick);
		ServerPlayConnectionEvents.DISCONNECT.register((listener, server) -> serverDisconnect(listener));

	}

	public record ServerboundKeyStatesUpdatePacket(Object2BooleanMap<String> states) implements CustomPacketPayload {

		private static final StreamCodec<ByteBuf, Object2BooleanMap<String>> STATES_CODEC = ByteBufCodecs.map(Object2BooleanOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.BOOL);

		public static final Type<ServerboundKeyStatesUpdatePacket> TYPE = new Type<>(NeoApoli.id("c2s/synchronize_key_states"));
		public static final StreamCodec<ByteBuf, ServerboundKeyStatesUpdatePacket> CODEC = STATES_CODEC.map(ServerboundKeyStatesUpdatePacket::new, ServerboundKeyStatesUpdatePacket::states);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public void handle(ServerPlayer player) {

			ServerLevel level = player.serverLevel();
			UUID uuid = player.getUUID();

			Map<String, KeyState> previous = PREVIOUS_STATES.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());
			Map<String, KeyState> current = CURRENT_STATES.get().computeIfAbsent(uuid, k -> new Object2ObjectLinkedOpenHashMap<>());

			for (var entry : states().object2BooleanEntrySet()) {

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

	}

}
