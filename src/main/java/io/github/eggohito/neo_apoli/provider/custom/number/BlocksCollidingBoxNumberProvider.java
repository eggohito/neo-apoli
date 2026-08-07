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
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public record BlocksCollidingBoxNumberProvider(Condition condition, BoxProvider box) implements NumberProvider {

	public static final Context.Parameter<CachedBlock> BLOCK_COLLIDING_BOX = NeoApoliContextParams.registerSimpleInternal("block_colliding_box", CachedBlock.class);
	public static final ContextKeySet CONDITION_PARAMETER_SET = new ContextKeySet.Builder().required(BLOCK_COLLIDING_BOX).build();

	public static final MapCodec<BlocksCollidingBoxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Condition.CODEC.fieldOf("condition").forGetter(BlocksCollidingBoxNumberProvider::condition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksCollidingBoxNumberProvider::box)
	).apply(instance, BlocksCollidingBoxNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlocksCollidingBoxNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Condition.STREAM_CODEC, BlocksCollidingBoxNumberProvider::condition,
		BoxProvider.STREAM_CODEC, BlocksCollidingBoxNumberProvider::box,
		BlocksCollidingBoxNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.BLOCKS_COLLIDING_BOX;
	}

	@Override
	public double getDouble(Context context) {

		Level level = context.level();
		int matches = 0;

		try {

			Context boxContext = context.forChild(".box");
			AABB box = box().getBox(boxContext).orElseThrow();

			CollisionContext collisionContext = box().getCollisionContext(boxContext);
			BlockCollisions<BlockPos> spliterator = new BlockCollisions<>(level, collisionContext, box, false, (pos, ignored) -> pos);

			while (spliterator.hasNext()) {

				try {

					CachedBlock block = CachedBlock.fromLoadedPos(level, spliterator.next());
					Context blockContext = new Context.Builder(context)
						.withRequired(BLOCK_COLLIDING_BOX, block)
						.build(level);

					if (condition().test(blockContext.forChild(".condition"))) {
						matches++;
					}

				}

				catch (PosUnloadedException | PosOutOfBoundsException ignored) {
					//  No-op
				}

			}

		}

		catch (NoSuchElementException ignored) {
			//  No-op
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
