package io.github.eggohito.neo_apoli.action.manager;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@ApiStatus.NonExtendable
public class ActionManager {

	public static final ResourceLocation ID = NeoApoli.id("manager/action");

	protected static volatile ImmutableMap<ResourceLocation, ActionHolder<?>> actions = ImmutableMap.of();
	protected static volatile ImmutableMap<ResourceLocation, List<ActionHolder<?>>> tags = ImmutableMap.of();

	public ActionManager() {
		//  Disallow extending non-internal classes
		var ignored = (ServerActionManager) this;
	}

	public static DataResult<ActionHolder<?>> getAsResult(ResourceLocation id) {
		var candidate = actions.get(id);
		return candidate != null
			? DataResult.success(candidate)
			: DataResult.error(() -> "Unknown action: \"" + id + "\"");
	}

	public static ActionHolder<?> get(ResourceLocation id) {
		return getAsResult(id).getOrThrow();
	}

	public static DataResult<ResourceLocation> getIdAsResult(Action action) {

		for (var candidate : actions.entrySet()) {

			if (candidate.getValue().value() == action) {
				return DataResult.success(candidate.getKey());
			}

		}

		return DataResult.error(() -> "No ID found for " + action);

	}

	public static ResourceLocation getId(Action action) {
		return getIdAsResult(action).getOrThrow();
	}

	public static DataResult<List<ActionHolder<?>>> getTag(ResourceLocation id) {
		var candidates = tags.get(id);
		return candidates != null
			? DataResult.success(candidates)
			: DataResult.error(() -> "Unknown action tag: \"" + id + "\"");
	}

	public static List<ActionHolder<?>> getTagOrEmpty(ResourceLocation id) {
		return getTag(id)
			.result()
			.orElseGet(List::of);
	}

	public static Iterable<ResourceLocation> ids() {
		return actions.keySet();
	}

	public static Iterable<ActionHolder<?>> actions() {
		return actions.values();
	}

	public static Iterable<ResourceLocation> tags() {
		return tags.keySet();
	}

	public static boolean contains(ResourceLocation id) {
		return getAsResult(id).isSuccess();
	}

	public static boolean containsId(Action action) {
		return getIdAsResult(action).isSuccess();
	}

	public record ClientboundUpdatePacket(Map<ResourceLocation, ActionHolder<?>> actions, Map<ResourceLocation, List<ActionHolder<?>>> tags) implements CustomPacketPayload {

		public static final Type<ClientboundUpdatePacket> TYPE = new Type<>(ID.withPath(path -> "clientbound/" + path + "/update"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdatePacket> CODEC = StreamCodec.ofMember(ClientboundUpdatePacket::send, ClientboundUpdatePacket::receive);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		private static ClientboundUpdatePacket receive(RegistryFriendlyByteBuf buf) {

			Map<ResourceLocation, ActionHolder<?>> powers = new Object2ObjectLinkedOpenHashMap<>();
			int powersCount = buf.readInt();

			for (int i = 0; i < powersCount; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);

				try {
					powers.put(id, ActionHolder.STREAM_CODEC.decode(buf));
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't decode {} during the syncing process", id, e);
					throw e;
				}

			}

			Map<ResourceLocation, List<ActionHolder<?>>> tags = new Object2ObjectLinkedOpenHashMap<>();
			int tagsCount = buf.readInt();

			for (int i = 0; i < tagsCount; i++) {

				ResourceLocation id = ResourceLocation.STREAM_CODEC.decode(buf);
				int count = buf.readInt();

				for (int j = 0; j < count; j++) {

					try {

						ResourceLocation holderId = ResourceLocation.STREAM_CODEC.decode(buf);
						ActionHolder<?> holder = Objects.requireNonNull(powers.get(holderId), "Unknown " + holderId);

						tags
							.computeIfAbsent(id, k -> new ObjectArrayList<>())
							.add(holder);

					}

					catch (Exception e) {
						NeoApoli.LOGGER.error("Couldn't decode action tag \"{}\" during the syncing process", id, e);
						throw e;
					}

				}

			}

			return new ClientboundUpdatePacket(powers, tags);

		}

		private void send(RegistryFriendlyByteBuf buf) {

			buf.writeInt(actions().size());

			for (var actionEntry : actions().entrySet()) {

				ResourceLocation id = actionEntry.getKey();
				ResourceLocation.STREAM_CODEC.encode(buf, id);

				try {
					ActionHolder.STREAM_CODEC.encode(buf, actionEntry.getValue());
				}

				catch (Exception e) {
					NeoApoli.LOGGER.error("Couldn't encode action \"{}\" during the syncing process", id, e);
					throw e;
				}

			}

			buf.writeInt(tags().size());

			for (var tagEntry : tags().entrySet()) {

				ResourceLocation id = tagEntry.getKey();
				ResourceLocation.STREAM_CODEC.encode(buf, id);

				List<ActionHolder<?>> holders = tagEntry.getValue();
				buf.writeInt(holders.size());

				for (var holder : holders) {
					ResourceLocation.STREAM_CODEC.encode(buf, holder.id());
				}

			}

		}

	}

}
