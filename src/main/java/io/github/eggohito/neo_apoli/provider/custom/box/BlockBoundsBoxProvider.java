package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliBoxProviderTypes;
import io.github.eggohito.neo_apoli.util.AABBUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record BlockBoundsBoxProvider(ClipContext.Block shapeType) implements BoxProvider {

	public static final MapCodec<BlockBoundsBoxProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.BLOCK_CLIP_CONTEXT.optionalFieldOf("shape_type", ClipContext.Block.OUTLINE).forGetter(BlockBoundsBoxProvider::shapeType))
		.apply(instance, BlockBoundsBoxProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockBoundsBoxProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.BLOCK_CLIP_CONTEXT, BlockBoundsBoxProvider::shapeType,
		BlockBoundsBoxProvider::new
	);

	@Override
	public @NotNull BoxProvider.Type<?> getType() {
		return NeoApoliBoxProviderTypes.BLOCK_BOUNDS;
	}

	@Override
	public @NotNull AABB nextBox(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {

			if (!context.hasParameter(NeoApoliContextParams.BLOCK_POS)) {
				context.reportProblem("Couldn't get bounding box of a block without its position!");
			}

			if (!context.hasParameter(NeoApoliContextParams.BLOCK_STATE)) {
				context.reportProblem("Couldn't get bounding box of a non-existing block!");
			}

			return AABBUtil.EMPTY;

		}

		BlockPos blockPos = context.getRequired(NeoApoliContextParams.BLOCK_POS);
		BlockState blockState = context.getRequired(NeoApoliContextParams.BLOCK_STATE);

		VoxelShape shape = shapeType().get(blockState, context.level(), blockPos, CollisionContext.empty());
		AABB bounds = shape.isEmpty()
			? AABBUtil.EMPTY
			: shape.bounds();

		return bounds.move(blockPos);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_POS, NeoApoliContextParams.BLOCK_STATE);
	}

}
