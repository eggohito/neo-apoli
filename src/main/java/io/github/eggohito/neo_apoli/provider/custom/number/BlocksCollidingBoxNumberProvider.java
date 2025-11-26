package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

public record BlocksCollidingBoxNumberProvider(BlockCondition blockCondition, BoxProvider box) implements NumberProvider {

	public static final MapCodec<BlocksCollidingBoxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksCollidingBoxNumberProvider::blockCondition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksCollidingBoxNumberProvider::box)
	).apply(instance, BlocksCollidingBoxNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksCollidingBoxNumberProvider> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, BlocksCollidingBoxNumberProvider::blockCondition,
		BoxProvider.STREAM_CODEC, BlocksCollidingBoxNumberProvider::box,
		BlocksCollidingBoxNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_COLLIDING_BOX;
	}

	@Override
	public @NotNull Number next(Context context) {

		Level world = context.getWorld();
		int matches = 0;

		Context boxContext = context.makeChild(".box");
		AABB box = box().next(boxContext);

		if (boxContext.hasErrors()) {
			return matches;
		}

		CollisionContext shapeContext = box().getShapeContext(boxContext);
		BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(world, shapeContext, box, false, (pos, shape) -> pos);

		while (spliterator.hasNext()) {

			BlockPos blockPos = spliterator.next();
			Context blockContext = ContextImpl.of(context, builder -> builder
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
				.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
				.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(blockPos))
				.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

			if (blockCondition().test(blockContext.makeChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(ProblemReporter reporter) {

		NumberProvider.super.validate(reporter);
		blockCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.forChild(".block_condition"));

		box().validate(reporter.forChild(".box"));

	}

}
