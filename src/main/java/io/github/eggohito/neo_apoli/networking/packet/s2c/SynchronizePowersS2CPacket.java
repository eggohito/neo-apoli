package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

public record SynchronizePowersS2CPacket(Map<Identifier, Power> powers) implements CustomPayload {

	public static final Id<SynchronizePowersS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowersS2CPacket> CODEC = PacketCodec.of(SynchronizePowersS2CPacket::write, SynchronizePowersS2CPacket::read);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	private static SynchronizePowersS2CPacket read(RegistryByteBuf buf) {

		int size = buf.readVarInt();
		Map<Identifier, Power> powers = new Object2ObjectOpenHashMap<>();

		for (int i = 0; i < size; i++) {

			Identifier id = buf.readIdentifier();
			Power power = Power.BASE_PACKET_CODEC.decode(buf);

			powers.put(id, power);

		}

		return new SynchronizePowersS2CPacket(powers);

	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(powers().size());
		powers().forEach((id, power) -> {
			buf.writeIdentifier(id);
			Power.BASE_PACKET_CODEC.encode(buf, power);
		});
	}

}
