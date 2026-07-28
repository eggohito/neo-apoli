package io.github.eggohito.neo_apoli.network.packet.serverbound;

import io.github.eggohito.neo_apoli.key.manager.KeyStateManager;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ServerboundUpdateKeyStatesPacket(Object2BooleanMap<String> states) implements CustomPacketPayload {

	private static final StreamCodec<ByteBuf, Object2BooleanMap<String>> STATES_CODEC = ByteBufCodecs.map(Object2BooleanOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.BOOL);

	public static final Type<ServerboundUpdateKeyStatesPacket> TYPE = new Type<>(KeyStateManager.ID.withPath(path -> "serverbound/" + path + "/update"));
	public static final StreamCodec<ByteBuf, ServerboundUpdateKeyStatesPacket> CODEC = STATES_CODEC.map(ServerboundUpdateKeyStatesPacket::new, ServerboundUpdateKeyStatesPacket::states);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
