package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BlocksInRadiusNumberProvider(Condition condition, Vec3Provider position, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final Context.Parameter<CachedBlock> BLOCK_IN_RADIUS = NeoApoliContextParams.registerSimpleInternal("block_in_radius", CachedBlock.class);
	public static final ContextKeySet CONDITION_PARAMETER_SET = new ContextKeySet.Builder().required(BLOCK_IN_RADIUS).build();

	public static final MapCodec<BlocksInRadiusNumberProvider> CODEC = MapCodecUtil.lazy(BlocksInRadiusNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.fieldOf("condition").forGetter(BlocksInRadiusNumberProvider::condition),
		Vec3Provider.CODEC.fieldOf("position").forGetter(BlocksInRadiusNumberProvider::position),
		Shape.CODEC.fieldOf("shape").forGetter(BlocksInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(BlocksInRadiusNumberProvider::radius)
	).apply(instance, BlocksInRadiusNumberProvider::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksInRadiusNumberProvider> STREAM_CODEC = StreamCodecUtil.lazy(BlocksInRadiusNumberProvider.class.getSimpleName(), () -> StreamCodec.composite(
		Condition.STREAM_CODEC, BlocksInRadiusNumberProvider::condition,
		Vec3Provider.STREAM_CODEC, BlocksInRadiusNumberProvider::position,
		Shape.STREAM_CODEC, BlocksInRadiusNumberProvider::shape,
		NumberProvider.STREAM_CODEC, BlocksInRadiusNumberProvider::radius,
		BlocksInRadiusNumberProvider::new
	));

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.BLOCKS_IN_RADIUS;
	}

	@Override
	public double getDouble(Context context) {

		Level level = context.level();
		int matches = 0;

		Context positionContext = context.forChild(".position");
		Vec3 position = position().getVec3(positionContext);

		if (positionContext.hasErrors()) {
			return matches;
		}

		Context radiusContext = context.forChild(".radius");
		int radius = radius().getInt(radiusContext);

		if (radiusContext.hasErrors()) {
			return matches;
		}

		for (var blockPos : shape().getBlockPositions(BlockPos.containing(position), radius)) {

			try {

				CachedBlock block = CachedBlock.fromLoadedPos(level, blockPos);
				Context blockContext = new Context.Builder(context)
					.withRequired(BLOCK_IN_RADIUS, block)
					.build(level);

				if (condition().test(blockContext.forChild(".condition"))) {
					matches++;
				}

			}

			catch (PosUnloadedException | PosOutOfBoundsException e) {
				context.reportProblem(e.getMessage());
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMETER_SET).forChild(".condition"));
		position().validate(validator.forChild(".position"));
		radius().validate(validator.forChild(".radius"));
	}

}
