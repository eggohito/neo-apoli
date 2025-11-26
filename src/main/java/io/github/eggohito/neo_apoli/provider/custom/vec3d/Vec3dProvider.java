package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public interface Vec3dProvider extends ValueProvider<Vec3> {

	Codec<Vec3dProvider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Vec3dProviderType.CODEC.dispatch(Vec3dProvider::getType, Vec3dProviderType::mapCodec), ConstantVec3dProvider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, Vec3dProvider> STREAM_CODEC = Vec3dProviderType.STREAM_CODEC.dispatch(Vec3dProvider::getType, Vec3dProviderType::packetCodec);

	@Override
	Vec3dProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Vec3d provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.VEC3D_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
