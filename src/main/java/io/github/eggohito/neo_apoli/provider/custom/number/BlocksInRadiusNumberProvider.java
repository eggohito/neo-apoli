package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BlocksInRadiusNumberProvider(BlockCondition blockCondition, Vec3Provider position, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<BlocksInRadiusNumberProvider> CODEC = MapCodecUtil.lazy(BlocksInRadiusNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksInRadiusNumberProvider::blockCondition),
		Vec3Provider.CODEC.fieldOf("position").forGetter(BlocksInRadiusNumberProvider::position),
		Shape.CODEC.fieldOf("shape").forGetter(BlocksInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(BlocksInRadiusNumberProvider::radius)
	).apply(instance, BlocksInRadiusNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksInRadiusNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(BlocksInRadiusNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		BlockCondition.STREAM_CODEC, BlocksInRadiusNumberProvider::blockCondition,
		Vec3Provider.STREAM_CODEC, BlocksInRadiusNumberProvider::position,
		Shape.STREAM_CODEC, BlocksInRadiusNumberProvider::shape,
		NumberProvider.STREAM_CODEC, BlocksInRadiusNumberProvider::radius,
		BlocksInRadiusNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_IN_RADIUS;
	}

	@Override
	public @NotNull Number next(Context context) {

		Level level = context.getLevel();
		int matches = 0;

		Context positionContext = context.forChild(".position");
		Vec3 position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return matches;
		}

		Context radiusContext = context.forChild(".radius");
		int radius = radius().nextInt(radiusContext);

		if (radiusContext.hasErrors()) {
			return matches;
		}

		for (var blockPos : shape().getBlockPositions(BlockPos.containing(position), radius)) {

			if (!level.hasChunkAt(blockPos)) {
				continue;
			}

			Context blockContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
				.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
				.add(NeoApoliContextKeys.BLOCK_STATE, level.getBlockState(blockPos))
				.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, level.getBlockEntity(blockPos))
				.build(level);

			if (blockCondition().test(blockContext.forChild(".block_condition"))) {
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

		position().validate(reporter.forChild(".position"));
		radius().validate(reporter.forChild(".radius"));

	}

}
