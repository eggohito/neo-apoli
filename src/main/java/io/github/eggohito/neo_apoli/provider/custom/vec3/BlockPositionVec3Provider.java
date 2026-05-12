package io.github.eggohito.neo_apoli.provider.custom.vec3;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliVec3ProviderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public enum BlockPositionVec3Provider implements Vec3Provider {

	INSTANCE;

	public static final MapCodec<BlockPositionVec3Provider> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, BlockPositionVec3Provider> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public @NotNull Vec3Provider.Type<?> getType() {
		return NeoApoliVec3ProviderTypes.BLOCK_POSITION;
	}

	@Override
	public @NotNull Vec3 nextVec3(Context context) {
		return context.getOptional(NeoApoliContextParams.BLOCK_POS)
			.map(BlockPos::getCenter)
			.orElse(net.minecraft.world.phys.Vec3.ZERO);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS);
	}

}
