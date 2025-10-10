package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryAlias;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.context.ContextType;

public record PowerType<P extends Power>(ContextType contextType, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {

	public static final RegistryAlias<PowerType<?>> ALIASES = new RegistryAlias<>(NeoApoliRegistries.POWER_TYPE);

	public static final Codec<PowerType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, PowerType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.POWER_TYPE);

	public ContextImpl.Builder contextBuilder() {
		return new ContextImpl.Builder(this.contextType());
	}

}
