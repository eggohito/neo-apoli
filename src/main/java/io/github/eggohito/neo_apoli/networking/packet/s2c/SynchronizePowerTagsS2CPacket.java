package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record SynchronizePowerTagsS2CPacket(Map<Identifier, List<PowerEntry<?>>> powerTags) implements CustomPayload {

	public static final Id<SynchronizePowerTagsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_power_tags"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowerTagsS2CPacket> CODEC = PacketCodec.of(SynchronizePowerTagsS2CPacket::write, SynchronizePowerTagsS2CPacket::read);

	private static SynchronizePowerTagsS2CPacket read(RegistryByteBuf buf) {

		Map<Identifier, List<PowerEntry<?>>> powerTags = new Object2ObjectOpenHashMap<>();
		int tagsAndEntriesCount = buf.readVarInt();

		for (int i = 0; i < tagsAndEntriesCount; i++) {

			Identifier tagId = buf.readIdentifier();
			Set<PowerReference> references = buf.readCollection(ObjectOpenHashSet::new, PowerReference.PACKET_CODEC);

			for (var reference : references) {
				powerTags
					.computeIfAbsent(tagId, k -> new ObjectArrayList<>())
					.add(PowerManager.getEntry(reference));
			}

		}

		return new SynchronizePowerTagsS2CPacket(powerTags);

	}

	private void write(RegistryByteBuf buf) {

		Map<Identifier, Set<PowerReference>> tagsAndEntries = new Object2ObjectOpenHashMap<>();
		powerTags().forEach((tagId, entries) -> entries.forEach(entry -> tagsAndEntries
			.computeIfAbsent(tagId, k -> new ObjectOpenHashSet<>())
			.add(entry.reference())));

		buf.writeVarInt(tagsAndEntries.size());
		tagsAndEntries.forEach((tagId, entryReference) -> {
			buf.writeIdentifier(tagId);
			buf.writeCollection(entryReference, PowerReference.PACKET_CODEC);
		});

	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
