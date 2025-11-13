package io.github.eggohito.neo_apoli.provider.custom.vec3d;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BlockPositionVec3dProvider() implements Vec3dProvider {

	public static final MapCodec<BlockPositionVec3dProvider> CODEC = MapCodec.unit(BlockPositionVec3dProvider::new);
	public static final PacketCodec<RegistryByteBuf, BlockPositionVec3dProvider> PACKET_CODEC = PacketCodecUtil.unit(BlockPositionVec3dProvider::new);

	@Override
	public Vec3dProviderType<?> getType() {
		return Vec3dProviderTypes.BLOCK_POSITION;
	}

	@Override
	public @NotNull Vec3d next(Context context) {
		return context.optional(ContextParameters.BLOCK_POS)
			.map(BlockPos::toCenterPos)
			.orElse(Vec3d.ZERO);
	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.BLOCK_POS);
	}

}
