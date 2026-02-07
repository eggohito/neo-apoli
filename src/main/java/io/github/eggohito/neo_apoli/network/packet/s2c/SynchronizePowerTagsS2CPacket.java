package io.github.eggohito.neo_apoli.network.packet.s2c;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record SynchronizePowerTagsS2CPacket(Map<ResourceLocation, List<PowerEntry<?>>> powerTags) implements CustomPacketPayload {

	private static final StreamCodec<ByteBuf, PowerEntry<?>> ENTRY_CODEC = PowerReference.STREAM_CODEC.map(PowerManager::getEntry, PowerEntry::reference);
	private static final StreamCodec<RegistryFriendlyByteBuf, Map<ResourceLocation, List<PowerEntry<?>>>> TAGS_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.collection(ObjectArrayList::new, ENTRY_CODEC));

	public static final Type<SynchronizePowerTagsS2CPacket> TYPE = new Type<>(NeoApoli.id("s2c/synchronize_power_tags"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SynchronizePowerTagsS2CPacket> CODEC = TAGS_CODEC.map(SynchronizePowerTagsS2CPacket::new, SynchronizePowerTagsS2CPacket::powerTags);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
