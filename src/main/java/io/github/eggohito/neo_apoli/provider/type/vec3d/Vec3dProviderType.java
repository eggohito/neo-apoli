package io.github.eggohito.neo_apoli.provider.type.vec3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Vec3dProviderType<P extends Vec3dProvider>(MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final RegistryFixedAlias<Vec3dProviderType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.VEC3D_PROVIDER_TYPE);

	public static final Codec<Vec3dProviderType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, Vec3dProviderType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.VEC3D_PROVIDER_TYPE);

}
