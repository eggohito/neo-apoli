package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public record BlocksIntersectingBoxNumberProvider(Condition condition, BoxProvider box) implements NumberProvider {

	public static final Context.Parameter<CachedBlock> BLOCK_INTERSECTING_BOX = NeoApoliContextParams.registerSimpleInternal("block_intersecting_box", CachedBlock.class);
	public static final ContextKeySet CONDITION_PARAMETER_SET = new ContextKeySet.Builder().required(BLOCK_INTERSECTING_BOX).build();

	public static final MapCodec<BlocksIntersectingBoxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.fieldOf("condition").forGetter(BlocksIntersectingBoxNumberProvider::condition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksIntersectingBoxNumberProvider::box)
	).apply(instance, BlocksIntersectingBoxNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksIntersectingBoxNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Condition.STREAM_CODEC, BlocksIntersectingBoxNumberProvider::condition,
		BoxProvider.STREAM_CODEC, BlocksIntersectingBoxNumberProvider::box,
		BlocksIntersectingBoxNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.BLOCKS_INTERSECTING_BOX;
	}

	@Override
	public double getDouble(Context context) {

		Level level = context.level();
		int matches = 0;

		Context boxContext = context.forChild(".box");
		AABB box = box().nextBox(boxContext);

		if (boxContext.hasErrors()) {
			return matches;
		}

		for (var blockPos : BlockPos.betweenClosed(box)) {

			try {

				CachedBlock block = CachedBlock.fromLoadedPos(level, blockPos);
				Context blockContext = new Context.Builder(context)
					.withRequired(BLOCK_INTERSECTING_BOX, block)
					.build(level);

				if (condition().test(blockContext.forChild(".condition"))) {
					matches++;
				}

			}

			catch (PosUnloadedException | PosOutOfBoundsException ignored) {
				//  No-op
			}

		}

		return matches;

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMETER_SET).forChild(".condition"));
		box().validate(validator.forChild(".box"));
	}

}
