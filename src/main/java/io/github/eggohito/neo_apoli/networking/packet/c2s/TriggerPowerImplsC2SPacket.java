package io.github.eggohito.neo_apoli.networking.packet.c2s;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Set;

public record TriggerPowerImplsC2SPacket(Set<PowerReference> powerReferences) implements CustomPayload {

	public static final Id<TriggerPowerImplsC2SPacket> ID = new Id<>(NeoApoli.id("c2s/trigger_power_impls"));
	public static final PacketCodec<RegistryByteBuf, TriggerPowerImplsC2SPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.collection(ObjectOpenHashSet::new, PowerReference.PACKET_CODEC), TriggerPowerImplsC2SPacket::powerReferences,
		TriggerPowerImplsC2SPacket::new
	);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
