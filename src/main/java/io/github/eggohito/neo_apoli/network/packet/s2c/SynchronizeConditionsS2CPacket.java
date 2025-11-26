package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SynchronizeConditionsS2CPacket(Map<ResourceLocation, Condition> conditions) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, Condition>> CONDITIONS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, Condition.STREAM_CODEC);

	public static final Type<SynchronizeConditionsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_conditions"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizeConditionsS2CPacket> CODEC = CONDITIONS_CODEC.map(SynchronizeConditionsS2CPacket::new, SynchronizeConditionsS2CPacket::conditions);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
