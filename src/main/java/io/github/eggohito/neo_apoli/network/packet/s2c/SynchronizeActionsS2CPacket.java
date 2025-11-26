package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SynchronizeActionsS2CPacket(Map<ResourceLocation, Action> actions) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Action>> ACTIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Action.STREAM_CODEC);

	public static final Type<SynchronizeActionsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_actions"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeActionsS2CPacket> CODEC = ACTIONS_CODEC.map(SynchronizeActionsS2CPacket::new, SynchronizeActionsS2CPacket::actions);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
