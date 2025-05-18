package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.ConditionEntry;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Set;

public record SynchronizeConditionsS2CPacket(Set<ConditionEntry<?>> conditions) implements CustomPayload {

	public static final Id<SynchronizeConditionsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_conditions"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeConditionsS2CPacket> CODEC = PacketCodec.of(SynchronizeConditionsS2CPacket::write, SynchronizeConditionsS2CPacket::read);

	@SuppressWarnings("unchecked")
	private static SynchronizeConditionsS2CPacket read(RegistryByteBuf buf) {

		Set<ConditionEntry<?>> conditions = new ObjectOpenHashSet<>();
		int size = buf.readVarInt();

		for (int i = 0; i < size; i++) {

			Identifier id = buf.readIdentifier();
			ConditionCategory<Condition<?>> category = (ConditionCategory<Condition<?>>) ConditionCategory.PACKET_CODEC.decode(buf);

			conditions.add(new ConditionEntry<>(id, category.packetCodec().decode(buf)));

		}

		return new SynchronizeConditionsS2CPacket(conditions);

	}

	@SuppressWarnings("unchecked")
	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(conditions().size());
		conditions().forEach(entry -> {

			Condition<?> condition = entry.value();
			ConditionCategory<Condition<?>> category = (ConditionCategory<Condition<?>>) condition.getCategory();

			buf.writeIdentifier(entry.id());
			ConditionCategory.PACKET_CODEC.encode(buf, category);

			category.packetCodec().encode(buf, condition);

		});
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
