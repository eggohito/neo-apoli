package io.github.eggohito.neo_apoli.util.comparison.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import io.github.eggohito.neo_apoli.util.comparison.Comparison;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ComparisonType<C extends Comparison>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {

	public static final FixedRegistryAlias<ComparisonType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.COMPARISON_TYPE);

	public static final Codec<ComparisonType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, ComparisonType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.COMPARISON_TYPE);

}
