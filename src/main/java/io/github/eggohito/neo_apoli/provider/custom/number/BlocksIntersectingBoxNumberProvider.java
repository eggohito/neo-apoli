package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record BlocksIntersectingBoxNumberProvider(BlockCondition blockCondition, BoxProvider box) implements NumberProvider {

	private static final ContextKeySet CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<BlocksIntersectingBoxNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksIntersectingBoxNumberProvider::blockCondition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksIntersectingBoxNumberProvider::box)
	).apply(instance, BlocksIntersectingBoxNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksIntersectingBoxNumberProvider> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, BlocksIntersectingBoxNumberProvider::blockCondition,
		BoxProvider.STREAM_CODEC, BlocksIntersectingBoxNumberProvider::box,
		BlocksIntersectingBoxNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_INTERSECTING_BOX;
	}

	@Override
	public @NotNull Number next(Context context) {

		Level level = context.level();
		int matches = 0;

		Context boxContext = context.forChild(".box");
		AABB box = box().next(boxContext);

		if (boxContext.hasErrors()) {
			return matches;
		}

		for (var blockPos : BlockPos.betweenClosed(box)) {

			if (!level.hasChunkAt(blockPos)) {
				continue;
			}

			Context blockContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
				.build(level);

			if (blockCondition().test(blockContext.forChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {

		NumberProvider.super.validate(validator);

		blockCondition().validate(validator.withAdditionalKeysFromSets(CONDITION_CONTEXT).forChild(".block_condition"));
		box().validate(validator.forChild(".box"));

	}

}
