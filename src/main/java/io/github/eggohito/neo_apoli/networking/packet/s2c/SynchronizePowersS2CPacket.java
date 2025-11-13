package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;
import java.util.Set;

public record SynchronizePowersS2CPacket(Set<PowerEntry<?>> powers) implements CustomPayload {

	private static final PacketCodec<RegistryByteBuf, List<PowerEntry<?>>> LIST_CODEC = PacketCodecs.collection(ObjectArrayList::new, PowerEntry.PACKET_CODEC);
	private static final PacketCodec<RegistryByteBuf, Set<PowerEntry<?>>> SET_CODEC = LIST_CODEC.xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Id<SynchronizePowersS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowersS2CPacket> CODEC = SET_CODEC.xmap(SynchronizePowersS2CPacket::new, SynchronizePowersS2CPacket::powers);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
