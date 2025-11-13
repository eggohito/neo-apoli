package io.github.eggohito.neo_apoli.provider.type.vec3d;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.ValueProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record Vec3dProviderType<P extends Vec3dProvider>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) implements ValueProviderType<P> {

	public static final RegistryFixedAlias<Vec3dProviderType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.VEC3D_PROVIDER_TYPE);

	public static final Codec<Vec3dProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, Vec3dProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.VEC3D_PROVIDER_TYPE);

}
