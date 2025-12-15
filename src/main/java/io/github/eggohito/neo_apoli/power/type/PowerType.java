package io.github.eggohito.neo_apoli.power.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;

public record PowerType<P extends Power>(ContextKeySet keySet, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {

	public static final RegistryFixedAlias<PowerType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.POWER_TYPE);

	public static final Codec<PowerType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, PowerType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.POWER_TYPE);

	public Context.Builder contextBuilder() {
		return new Context.Builder(this.keySet());
	}

}
