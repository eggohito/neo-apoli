package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3ProviderType;
import io.github.eggohito.neo_apoli.provider.type.vec3d.Vec3dProviderTypes;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BlockPositionVec3Provider() implements Vec3Provider {

	public static final MapCodec<BlockPositionVec3Provider> CODEC = MapCodec.unit(BlockPositionVec3Provider::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockPositionVec3Provider> STREAM_CODEC = StreamCodecUtil.unit(BlockPositionVec3Provider::new);

	@Override
	public Vec3ProviderType<?> getType() {
		return Vec3dProviderTypes.BLOCK_POSITION;
	}

	@Override
	public @NotNull Vec3 next(Context context) {
		return context.optional(NeoApoliContextKeys.BLOCK_POS)
			.map(BlockPos::getCenter)
			.orElse(Vec3.ZERO);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_POS);
	}

}
