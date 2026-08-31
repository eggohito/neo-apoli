package io.github.eggohito.neo_apoli.network.packet.clientbound;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.duck.internal.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ClientboundPowerRecipeDisplaysUpdatePacket(Int2ObjectMap<PowerIdentifier> displays) implements CustomPacketPayload {

	private static final StreamCodec<RegistryFriendlyByteBuf, Int2ObjectMap<PowerIdentifier>> RECIPE_CODEC = ByteBufCodecs.map(Int2ObjectOpenHashMap::new, ByteBufCodecs.INT, PowerIdentifier.STREAM_CODEC);

	public static final Type<ClientboundPowerRecipeDisplaysUpdatePacket> TYPE = new Type<>(NeoApoli.id("clientbound/update_power_recipe_displays"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPowerRecipeDisplaysUpdatePacket> CODEC = RECIPE_CODEC.map(ClientboundPowerRecipeDisplaysUpdatePacket::new, ClientboundPowerRecipeDisplaysUpdatePacket::displays);

	@Override
	public @NotNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(PowerRecipeDisplayHolder holder) {
		holder.neo_apoli$setPowerIdsByIndex(this.displays());
	}

}
