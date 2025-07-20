package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.ActionEntry;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record SynchronizeActionTagsS2CPacket(Map<ActionCategory<?>, Map<Identifier, List<ActionEntry<?>>>> actionTags) implements CustomPayload {

	public static final Id<SynchronizeActionTagsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_action_tags"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeActionTagsS2CPacket> CODEC = PacketCodec.of(SynchronizeActionTagsS2CPacket::write, SynchronizeActionTagsS2CPacket::read);

	private static SynchronizeActionTagsS2CPacket read(RegistryByteBuf buf) {

		Map<ActionCategory<?>, Map<Identifier, List<ActionEntry<?>>>> actionTags = new Object2ObjectOpenHashMap<>();
		int categoriesAndTagIds = buf.readVarInt();

		for (int i = 0; i < categoriesAndTagIds; i++) {

			ActionCategory<?> category = ActionCategories.PACKET_CODEC.decode(buf);
			int tagIdsAndEntryIdsCount = buf.readVarInt();

			for (int j = 0; j < tagIdsAndEntryIdsCount; j++) {

				Identifier tagId = buf.readIdentifier();
				Set<Identifier> entryIds = buf.readCollection(ObjectOpenHashSet::new, PacketByteBuf::readIdentifier);

				for (var entryId : entryIds) {
					actionTags
						.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>())
						.computeIfAbsent(tagId, k -> new ObjectArrayList<>())
						.add(ActionManager.getEntry(category, entryId));
				}

			}

		}

		return new SynchronizeActionTagsS2CPacket(actionTags);

	}

	private void write(RegistryByteBuf buf) {

		Map<ActionCategory<?>, Map<Identifier, Set<Identifier>>> categoriesAndTagIds = new Object2ObjectOpenHashMap<>();
		actionTags().forEach((category, tagsAndEntries) -> tagsAndEntries.forEach((tagId, entries) -> entries.forEach(entry -> categoriesAndTagIds
			.computeIfAbsent(category, k -> new Object2ObjectOpenHashMap<>())
			.computeIfAbsent(tagId, k -> new ObjectOpenHashSet<>())
			.add(entry.id()))));

		buf.writeVarInt(categoriesAndTagIds.size());
		categoriesAndTagIds.forEach((category, tagIdsAndEntryIds) -> {

			ActionCategories.PACKET_CODEC.encode(buf, category);
			buf.writeVarInt(tagIdsAndEntryIds.size());

			tagIdsAndEntryIds.forEach((id, entries) -> {
				buf.writeIdentifier(id);
				buf.writeCollection(entries, PacketByteBuf::writeIdentifier);
			});

		});

	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
