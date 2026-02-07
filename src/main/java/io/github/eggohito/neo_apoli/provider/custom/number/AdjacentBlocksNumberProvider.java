package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record AdjacentBlocksNumberProvider(BlockCondition adjacentBlockCondition, Vec3Provider position) implements NumberProvider {

	private static final ContextKeySet CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<AdjacentBlocksNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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

		Level level = context.level();
		long matches = 0;

		Context positionContext = context.forChild(".position");
		BlockPos blockPos = BlockPos.containing(position().next(positionContext));

		if (positionContext.hasErrors()) {
			return matches;
		}

		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = blockPos.relative(direction);

			if (!level.hasChunkAt(offsetPos)) {
				continue;
			}

			Context blockContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.BLOCK_POS, offsetPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(offsetPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(offsetPos))
				.build(level);

			if (adjacentBlockCondition().test(blockContext.forChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		adjacentBlockCondition().validate(validator.withAdditionalKeysFromSets(CONDITION_CONTEXT).forChild(".block_condition"));
		position().validate(validator.forChild(".position"));

	}

}
