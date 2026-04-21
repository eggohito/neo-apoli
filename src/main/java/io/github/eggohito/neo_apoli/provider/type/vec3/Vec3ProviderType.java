package io.github.eggohito.neo_apoli.provider.type.vec3;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Vec3ProviderType<P extends Vec3Provider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) implements ValueProviderType<P> {

	public static final FixedRegistryAlias<Vec3ProviderType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.VEC3_PROVIDER_TYPE);

	public static final Codec<Vec3ProviderType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, Vec3ProviderType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.VEC3_PROVIDER_TYPE);

}
