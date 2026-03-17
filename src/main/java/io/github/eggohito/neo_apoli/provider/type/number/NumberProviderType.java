package io.github.eggohito.neo_apoli.provider.type.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record NumberProviderType<P extends NumberProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final FixedRegistryAlias<NumberProviderType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.NUMBER_PROVIDER_TYPE);

	public static final Codec<NumberProviderType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, NumberProviderType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.NUMBER_PROVIDER_TYPE);

}
