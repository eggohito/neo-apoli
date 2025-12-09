package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.provider.ValueProvider;
import io.github.eggohito.neo_apoli.provider.type.vec3.Vec3ProviderType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public interface Vec3Provider extends ValueProvider<Vec3> {

	Codec<Vec3Provider> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Vec3ProviderType.CODEC.dispatch(Vec3Provider::getType, Vec3ProviderType::mapCodec), ConstantVec3Provider.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, Vec3Provider> STREAM_CODEC = Vec3ProviderType.STREAM_CODEC.dispatch(Vec3Provider::getType, Vec3ProviderType::packetCodec);

	@Override
	Vec3ProviderType<?> getType();

	@Override
	default String asDisplayString() {
		return "Vec3d provider with type \"" + RegistryUtil.getId(NeoApoliRegistries.VEC3_PROVIDER_TYPE, this.getType()) + "\"";
	}

}
