package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

@SuppressWarnings("unchecked")
public record SynchronizeActionsS2CPacket(Map<ActionCategory<?>, Map<Identifier, Action>> actions) implements CustomPayload {

	public static final Id<SynchronizeActionsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_actions"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeActionsS2CPacket> CODEC = PacketCodec.of(SynchronizeActionsS2CPacket::write, SynchronizeActionsS2CPacket::read);

	private static SynchronizeActionsS2CPacket read(RegistryByteBuf buf) {

		Map<ActionCategory<?>, Map<Identifier, Action>> actions = new Object2ObjectOpenHashMap<>();
		int actionsCount = buf.readVarInt();

		for (int i = 0; i < actionsCount; i++) {

			ActionCategory<Action> category = (ActionCategory<Action>) ActionCategories.PACKET_CODEC.decode(buf);
			int entriesCount = buf.readVarInt();

			for (int j = 0; j < entriesCount; j++) {

				Identifier id = buf.readIdentifier();
				Action action = category.packetCodec().decode(buf);

				actions
					.computeIfAbsent(category, key -> new Object2ObjectOpenHashMap<>())
					.put(id, action);

			}

		}

		return new SynchronizeActionsS2CPacket(actions);

	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(actions().size());
		actions().forEach((category, entries) -> {

			ActionCategory<Action> castedCategory = (ActionCategory<Action>) category;
			ActionCategories.PACKET_CODEC.encode(buf, castedCategory);

			buf.writeVarInt(entries.size());
			entries.forEach((id, action) -> {
				buf.writeIdentifier(id);
				castedCategory.packetCodec().encode(buf, action);
			});

		});
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
