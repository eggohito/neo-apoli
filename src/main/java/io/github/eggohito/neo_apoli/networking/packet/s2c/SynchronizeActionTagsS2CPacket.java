package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionEntry;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public record SynchronizeActionTagsS2CPacket(Map<ActionCategory<?>, Map<Identifier, List<ActionEntry<?>>>> actionTags) implements CustomPayload {

	public static final Id<SynchronizeActionTagsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_action_tags"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeActionTagsS2CPacket> CODEC = PacketCodec.of(SynchronizeActionTagsS2CPacket::write, SynchronizeActionTagsS2CPacket::read);

	private static SynchronizeActionTagsS2CPacket read(RegistryByteBuf buf) {

		Map<ActionCategory<?>, Map<Identifier, List<ActionEntry<?>>>> actionTags = new Object2ObjectOpenHashMap<>();
		int actionTagsCount = buf.readVarInt();

		for (int i = 0; i < actionTagsCount; i++) {

			ActionCategory<Action<?>> category = (ActionCategory<Action<?>>) ActionCategory.PACKET_CODEC.decode(buf);
			int tagEntriesCount = buf.readVarInt();

			for (int j = 0; j < tagEntriesCount; j++) {

				Identifier tagId = buf.readIdentifier();
				int entriesCount = buf.readVarInt();

				for (int k = 0; k < entriesCount; k++) {

					Identifier id = buf.readIdentifier();
					Action<?> action = category.basePacketCodec().decode(buf);

					actionTags
						.computeIfAbsent(category, key -> new Object2ObjectOpenHashMap<>())
						.computeIfAbsent(tagId, key -> new ObjectArrayList<>())
						.add(new ActionEntry<>(id, action));

				}

			}

		}

		return new SynchronizeActionTagsS2CPacket(actionTags);

	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(actionTags().size());
		actionTags().forEach((category, tagEntries) -> {

			ActionCategory<Action<?>> castedCategory = (ActionCategory<Action<?>>) category;
			ActionCategory.PACKET_CODEC.encode(buf, castedCategory);

			buf.writeVarInt(tagEntries.size());
			tagEntries.forEach((tagId, entries) -> {

				buf.writeIdentifier(tagId);
				buf.writeVarInt(entries.size());

				entries.forEach(entry -> {
					buf.writeIdentifier(entry.id());
					castedCategory.basePacketCodec().encode(buf, entry.value());
				});

			});

		});
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
