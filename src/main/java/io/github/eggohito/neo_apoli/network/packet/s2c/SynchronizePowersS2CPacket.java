package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SynchronizePowersS2CPacket(Set<PowerEntry<?>> powers) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, List<PowerEntry<?>>> LIST_CODEC = ByteBufCodecs.collection(ObjectArrayList::new, PowerEntry.STREAM_CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, Set<PowerEntry<?>>> SET_CODEC = LIST_CODEC.map(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Type<SynchronizePowersS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowersS2CPacket> CODEC = SET_CODEC.map(SynchronizePowersS2CPacket::new, SynchronizePowersS2CPacket::powers);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
