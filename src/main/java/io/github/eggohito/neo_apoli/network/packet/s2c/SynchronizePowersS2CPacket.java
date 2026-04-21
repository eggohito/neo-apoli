package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;
import java.util.Set;

public record SynchronizePowersS2CPacket(Set<PowerHolder<?>> powers) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, List<PowerHolder<?>>> LIST_CODEC = ByteBufCodecs.collection(ObjectArrayList::new, PowerHolder.STREAM_CODEC);
	private static final StreamCodec<RegistryFriendlyByteBuf, Set<PowerHolder<?>>> SET_CODEC = LIST_CODEC.map(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Type<SynchronizePowersS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowersS2CPacket> CODEC = SET_CODEC.map(SynchronizePowersS2CPacket::new, SynchronizePowersS2CPacket::powers);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
