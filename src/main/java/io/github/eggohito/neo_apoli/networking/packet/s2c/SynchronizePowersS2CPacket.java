package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.PowerIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;

import java.util.Map;

public record SynchronizePowersS2CPacket(Map<PowerIdentifier, Power> powersById) implements CustomPayload {

	public static final Id<SynchronizePowersS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_powers"));
	public static final PacketCodec<RegistryByteBuf, SynchronizePowersS2CPacket> CODEC = PacketCodec.of(SynchronizePowersS2CPacket::write, SynchronizePowersS2CPacket::read);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

	@SuppressWarnings("unchecked")
	private static SynchronizePowersS2CPacket read(RegistryByteBuf buf) {

		Map<PowerIdentifier, Power> powersById = new Object2ObjectOpenHashMap<>();
		int size = buf.readVarInt();

		for (int i = 0; i < size; i++) {

			PowerIdentifier id = PowerIdentifier.PACKET_CODEC.decode(buf);
			RegistryKey<PowerType<?>> powerTypeKey = buf.readRegistryKey(NeoApoliRegistryKeys.POWER_TYPE);

			PowerType<Power> powerType = (PowerType<Power>) NeoApoliRegistries.POWER_TYPE.getValueOrThrow(powerTypeKey);
			Power power = powerType.packetCodec().decode(buf);

			powersById.put(id, power);

		}

		return new SynchronizePowersS2CPacket(powersById);

	}

	@SuppressWarnings("unchecked")
	private void write(RegistryByteBuf buf) {

		buf.writeVarInt(powersById().size());
		powersById().forEach((id, power) -> {

			PowerType<Power> powerType = (PowerType<Power>) power.getType();
			RegistryKey<PowerType<?>> powerTypeKey = NeoApoliRegistries.POWER_TYPE.getKey(powerType).orElseThrow(() -> new IllegalStateException("Power \"" + id + "\" has a power type that isn't registered in the power type registry!"));

			PowerIdentifier.PACKET_CODEC.encode(buf, id);
			buf.writeRegistryKey(powerTypeKey);

			powerType.packetCodec().encode(buf, power);

		});

	}

}
