package io.github.eggohito.neo_apoli.provider.custom.box;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderType;
import io.github.eggohito.neo_apoli.provider.type.box.BoxProviderTypes;
import io.github.eggohito.neo_apoli.util.AABBUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
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

	public static final MapCodec<BlockBoundsBoxProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(NeoApoliCodecs.BLOCK_CLIP_CONTEXT.optionalFieldOf("shape_type", ClipContext.Block.OUTLINE).forGetter(BlockBoundsBoxProvider::shapeType))
		.apply(instance, BlockBoundsBoxProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockBoundsBoxProvider> STREAM_CODEC = StreamCodec.composite(
		NeoApoliStreamCodecs.BLOCK_CLIP_CONTEXT, BlockBoundsBoxProvider::shapeType,
		BlockBoundsBoxProvider::new
	);

	@Override
	public BoxProviderType<?> getType() {
		return BoxProviderTypes.BLOCK_BOUNDS;
	}

	@Override
	public @NotNull AABB next(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {

			if (!context.hasParameter(NeoApoliContextKeys.BLOCK_POS)) {
				context.getReporter().report("Couldn't get bounding box of a block without its position!");
			}

			if (!context.hasParameter(NeoApoliContextKeys.BLOCK_STATE)) {
				context.getReporter().report("Couldn't get bounding box of a non-existing block!");
			}

			return AABBUtil.EMPTY;

		}

		BlockPos blockPos = context.required(NeoApoliContextKeys.BLOCK_POS);
		BlockState blockState = context.required(NeoApoliContextKeys.BLOCK_STATE);

		VoxelShape shape = shapeType().get(blockState, context.getWorld(), blockPos, CollisionContext.empty());
		AABB bounds = shape.isEmpty()
			? AABBUtil.EMPTY
			: shape.bounds();

		return bounds.move(blockPos);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_POS, NeoApoliContextKeys.BLOCK_STATE);
	}

}
