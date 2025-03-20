package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.power.Power;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.Objects;

public record PowerEntry<P extends Power>(PowerReference reference, P value) {

	public static final PacketCodec<RegistryByteBuf, PowerEntry<?>> PACKET_CODEC = PacketCodec.tuple(
		PowerReference.PACKET_CODEC, PowerEntry::reference,
		Power.BASE_PACKET_CODEC, PowerEntry::value,
		PowerEntry::new
	);

	public boolean isSubPower() {
		return reference() instanceof PowerReference.SubPower;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof PowerEntry<?> that) {
			return Objects.equals(this.reference(), that.reference());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.reference());
	}

}
