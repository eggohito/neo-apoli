package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AdjacentBlocksNumberProvider(BlockCondition adjacentBlockCondition, Vec3Provider position) implements NumberProvider {

	public static final MapCodec<AdjacentBlocksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(AdjacentBlocksNumberProvider::adjacentBlockCondition),
		Vec3Provider.CODEC.fieldOf("position").forGetter(AdjacentBlocksNumberProvider::position)
	).apply(instance, AdjacentBlocksNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AdjacentBlocksNumberProvider> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, AdjacentBlocksNumberProvider::adjacentBlockCondition,
		Vec3Provider.STREAM_CODEC, AdjacentBlocksNumberProvider::position,
		AdjacentBlocksNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ADJACENT_BLOCKS_NUMBER_PROVIDER;
	}

	@Override
	public @NotNull Number next(Context context) {

		Level world = context.getWorld();
		long matches = 0;

		Context positionContext = context.makeChild(".position");
		BlockPos blockPos = BlockPos.containing(position().next(positionContext));

		if (positionContext.hasErrors()) {
			return matches;
		}

		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = blockPos.relative(direction);

			if (!world.hasChunkAt(offsetPos)) {
				continue;
			}

			Context blockContext = ContextImpl.of(context, builder -> builder
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
				.add(NeoApoliContextKeys.BLOCK_POS, offsetPos)
				.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(offsetPos))
				.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(offsetPos)));

			if (adjacentBlockCondition().test(blockContext.makeChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(ProblemReporter reporter) {

		NumberProvider.super.validate(reporter);
		adjacentBlockCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.forChild(".block_condition"));

		position().validate(reporter.forChild(".position"));

	}

}
