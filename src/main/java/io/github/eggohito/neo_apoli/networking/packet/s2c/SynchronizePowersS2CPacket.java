package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.internal.MultiplePower;
import io.github.eggohito.neo_apoli.util.PowerEntry;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SynchronizePowersS2CPacket(ObjectSet<PowerEntry<?>> powers) implements CustomPayload {

	public static final Id<SynchronizePowersS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowersS2CPacket> CODEC = PacketCodec.of(SynchronizePowersS2CPacket::write, SynchronizePowersS2CPacket::read);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	private static SynchronizePowersS2CPacket read(RegistryByteBuf buf) {

		int size = buf.readVarInt();
		ObjectSet<PowerEntry<?>> powers = new ObjectOpenHashSet<>();

		for (int i = 0; i < size; i++) {

			PowerEntry<?> entry = PowerEntry.PACKET_CODEC.decode(buf);

			PowerReference reference = entry.reference();
			Power power = entry.value();

			if (power instanceof MultiplePower multiplePower && reference instanceof PowerReference.Power powerReference) {
				multiplePower.getSubPowers().forEach((name, subPower) -> powers.add(new PowerEntry<>(PowerReference.ofSubPower(powerReference.id(), name), subPower)));
			}

			powers.add(entry);

		}

		return new SynchronizePowersS2CPacket(powers);

	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(powers().size());
		powers.forEach(entry -> PowerEntry.PACKET_CODEC.encode(buf, entry));
	}

}
