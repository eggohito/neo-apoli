package io.github.eggohito.neo_apoli.networking.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionEntry;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Set;

public record SynchronizeActionsS2CPacket(Set<ActionEntry<?>> actions) implements CustomPayload {

	public static final Id<SynchronizeActionsS2CPacket> ID = new Id<>(NeoApoli.id("s2c/synchronize_actions"));
	public static final PacketCodec<RegistryByteBuf, SynchronizeActionsS2CPacket> CODEC = PacketCodec.of(SynchronizeActionsS2CPacket::write, SynchronizeActionsS2CPacket::read);

	@SuppressWarnings("unchecked")
	private static SynchronizeActionsS2CPacket read(RegistryByteBuf buf) {

		Set<ActionEntry<?>> actions = new ObjectOpenHashSet<>();
		int size = buf.readVarInt();

		for (int i = 0; i < size; i++) {

			Identifier id = buf.readIdentifier();
			ActionCategory<Action<?>> category = (ActionCategory<Action<?>>) ActionCategory.PACKET_CODEC.decode(buf);

			actions.add(new ActionEntry<>(id, category.packetCodec().decode(buf)));

		}

		return new SynchronizeActionsS2CPacket(actions);

	}

	@SuppressWarnings("unchecked")
	private void write(RegistryByteBuf buf) {
		buf.writeVarInt(actions().size());
		actions().forEach(entry -> {

			Action<?> action = entry.value();
			ActionCategory<Action<?>> category = (ActionCategory<Action<?>>) action.getCategory();

			buf.writeIdentifier(entry.id());
			ActionCategory.PACKET_CODEC.encode(buf, category);

			category.packetCodec().encode(buf, action);

		});

	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}

}
