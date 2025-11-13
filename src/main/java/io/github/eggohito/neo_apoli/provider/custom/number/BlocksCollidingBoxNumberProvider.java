package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.block.ShapeContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockCollisionSpliterator;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record BlocksCollidingBoxNumberProvider(BlockCondition blockCondition, BoxProvider box) implements NumberProvider {

	public static final MapCodec<BlocksCollidingBoxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksCollidingBoxNumberProvider::blockCondition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksCollidingBoxNumberProvider::box)
	).apply(instance, BlocksCollidingBoxNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, BlocksCollidingBoxNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, BlocksCollidingBoxNumberProvider::blockCondition,
		BoxProvider.PACKET_CODEC, BlocksCollidingBoxNumberProvider::box,
		BlocksCollidingBoxNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_COLLIDING_BOX;
	}

	@Override
	public @NotNull Number next(Context context) {

		World world = context.getWorld();
		int matches = 0;

		Context boxContext = context.makeChild(".box");
		Box box = box().next(boxContext);

		if (boxContext.hasErrors()) {
			return matches;
		}

		ShapeContext shapeContext = box().getShapeContext(boxContext);
		BlockCollisionSpliterator<BlockPos> spliterator = new BlockCollisionSpliterator<>(world, shapeContext, box, false, (pos, shape) -> pos);

		while (spliterator.hasNext()) {

			BlockPos blockPos = spliterator.next();
			Context blockContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
				.add(ContextParameters.BLOCK_POS, blockPos)
				.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

			if (blockCondition().test(blockContext.makeChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);
		blockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".block_condition"));

		box().validate(reporter.makeChild(".box"));

	}

}
