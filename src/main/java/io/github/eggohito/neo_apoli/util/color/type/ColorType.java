package io.github.eggohito.neo_apoli.util.color.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import io.github.eggohito.neo_apoli.util.color.Color;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ColorType<C extends Color>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {

	public static final FixedRegistryAlias<ColorType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.COLOR_TYPE);

	public static final Codec<ColorType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, ColorType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.COLOR_TYPE);

}
