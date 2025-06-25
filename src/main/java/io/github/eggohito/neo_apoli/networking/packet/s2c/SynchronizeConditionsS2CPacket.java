package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

@SuppressWarnings("unchecked")
public record SynchronizeConditionsS2CPacket(Map<ConditionCategory<?>, Map<Identifier, Condition>> conditions) implements CustomPayload {

	public static final Id<SynchronizeConditionsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_conditions"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeConditionsS2CPacket> CODEC = PacketCodec.of(SynchronizeConditionsS2CPacket::write, SynchronizeConditionsS2CPacket::read);

	private static SynchronizeConditionsS2CPacket read(RegistryByteBuf buf) {

		Map<ConditionCategory<?>, Map<Identifier, Condition>> conditions = new Object2ObjectOpenHashMap<>();
		int conditionsCount = buf.readVarInt();

		for (int i = 0; i < conditionsCount; i++) {

			ConditionCategory<Condition> category = (ConditionCategory<Condition>) ConditionCategory.PACKET_CODEC.decode(buf);
			int entriesCount = buf.readVarInt();

			for (int j = 0; j < entriesCount; j++) {

				Identifier id = buf.readIdentifier();
				Condition condition = category.basePacketCodec().decode(buf);

				conditions.computeIfAbsent(category, key -> new Object2ObjectOpenHashMap<>()).put(id, condition);

			}

		}

		return new SynchronizeConditionsS2CPacket(conditions);

	}

	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(conditions().size());
		conditions().forEach((category, entries) -> {

			ConditionCategory<Condition> castedCategory = (ConditionCategory<Condition>) category;
			ConditionCategory.PACKET_CODEC.encode(buf, castedCategory);

			buf.writeVarInt(entries.size());
			entries.forEach((id, condition) -> {
				buf.writeIdentifier(id);
				castedCategory.basePacketCodec().encode(buf, condition);
			});

		});
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
