package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionHolder;
import io.github.eggohito.neo_apoli.action.manager.ActionManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ClientboundUpdateActionsPacket(Map<ResourceLocation, ActionHolder<?>> actions, Map<ResourceLocation, List<ActionHolder<?>>> tags) implements CustomPacketPayload {

	public static final Type<ClientboundUpdateActionsPacket> TYPE = new Type<>(ActionManager.ID.withPath(path -> "clientbound/" + path + "/update"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateActionsPacket> CODEC = StreamCodec.ofMember(ClientboundUpdateActionsPacket::send, ClientboundUpdateActionsPacket::receive);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private static ClientboundUpdateActionsPacket receive(RegistryFriendlyByteBuf buf) {

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

		return new ClientboundUpdateActionsPacket(powers, tags);

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
