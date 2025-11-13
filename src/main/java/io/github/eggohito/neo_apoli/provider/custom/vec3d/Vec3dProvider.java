package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.Vec3d;

public interface Vec3dProvider extends ValueProvider<Vec3d> {

	Codec<Vec3dProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Vec3dProviderType.CODEC.dispatch(Vec3dProvider::getType, Vec3dProviderType::mapCodec), ConstantVec3dProvider.INLINE_CODEC));

	PacketCodec<RegistryByteBuf, Vec3dProvider> PACKET_CODEC = Vec3dProviderType.PACKET_CODEC.dispatch(Vec3dProvider::getType, Vec3dProviderType::packetCodec);

	@Override
	Vec3dProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Vec3d provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.VEC3D_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
