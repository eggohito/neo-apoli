package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record SynchronizePowerTagsS2CPacket(Map<Identifier, List<PowerEntry<?>>> powerTags) implements CustomPayload {

	public static final Id<SynchronizePowerTagsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_power_tags"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowerTagsS2CPacket> CODEC = PacketCodec.of(SynchronizePowerTagsS2CPacket::write, SynchronizePowerTagsS2CPacket::read);

	private static final PacketCodec<RegistryByteBuf, List<PowerEntry<?>>> ENTRIES_CODEC = PacketCodecs.collection(ObjectArrayList::new, PowerEntry.PACKET_CODEC);

	private static SynchronizePowerTagsS2CPacket read(RegistryByteBuf buf) {
		return new SynchronizePowerTagsS2CPacket(buf.readMap(PacketByteBuf::readIdentifier, ignored -> ENTRIES_CODEC.decode(buf)));
	}

	private void write(RegistryByteBuf buf) {
		buf.writeMap(powerTags(), PacketByteBuf::writeIdentifier, (ignored, entries) -> ENTRIES_CODEC.encode(buf, entries));
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
