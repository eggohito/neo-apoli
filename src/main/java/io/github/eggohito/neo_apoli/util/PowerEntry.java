package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

public record PowerEntry<P extends Power>(PowerIdentifier id, P value) {

	public static final PacketCodec<RegistryByteBuf, PowerEntry<?>> PACKET_CODEC = PacketCodec.tuple(
		PowerIdentifier.PACKET_CODEC, PowerEntry::id,
		Power.BASE_PACKET_CODEC, PowerEntry::value,
		PowerEntry::new
	);

	public boolean isSubPower() {
		return id() instanceof PowerIdentifier.SubPower;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerEntry<?> that) {
			return Objects.equals(this.id(), that.id());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id());
	}

}
