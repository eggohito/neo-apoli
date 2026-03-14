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
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

public record BlocksCollidingBoxNumberProvider(BlockCondition blockCondition, BoxProvider box) implements NumberProvider {

	private static final ContextKeySet CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<BlocksCollidingBoxNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksCollidingBoxNumberProvider::blockCondition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksCollidingBoxNumberProvider::box)
	).apply(instance, BlocksCollidingBoxNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksCollidingBoxNumberProvider> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, BlocksCollidingBoxNumberProvider::blockCondition,
		BoxProvider.STREAM_CODEC, BlocksCollidingBoxNumberProvider::box,
		BlocksCollidingBoxNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_COLLIDING_BOX;
	}

	@Override
	public double nextDouble(Context context) {

		Level level = context.level();
		int matches = 0;

		Context boxContext = context.forChild(".box");
		AABB box = box().nextBox(boxContext);

		if (boxContext.hasErrors()) {
			return matches;
		}

		CollisionContext shapeContext = box().getCollisionContext(boxContext);
		BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(level, shapeContext, box, false, (pos, shape) -> pos);

		while (spliterator.hasNext()) {

			BlockPos blockPos = spliterator.next();
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
