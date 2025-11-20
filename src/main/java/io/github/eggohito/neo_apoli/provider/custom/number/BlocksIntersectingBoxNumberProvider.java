package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.box.BoxProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record BlocksIntersectingBoxNumberProvider(BlockCondition blockCondition, BoxProvider box) implements NumberProvider {

	public static final MapCodec<BlocksIntersectingBoxNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksIntersectingBoxNumberProvider::blockCondition),
		BoxProvider.CODEC.fieldOf("box").forGetter(BlocksIntersectingBoxNumberProvider::box)
	).apply(instance, BlocksIntersectingBoxNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, BlocksIntersectingBoxNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, BlocksIntersectingBoxNumberProvider::blockCondition,
		BoxProvider.PACKET_CODEC, BlocksIntersectingBoxNumberProvider::box,
		BlocksIntersectingBoxNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_INTERSECTING_BOX;
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

		for (var blockPos : BlockPos.iterate(box)) {

			if (!world.isChunkLoaded(blockPos)) {
				continue;
			}

			Context blockContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), NeoApoliContextTypes.BLOCK))
				.add(NeoApoliContextParameters.BLOCK_POS, blockPos)
				.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
				.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

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
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.BLOCK))
			.makeChild(".block_condition"));

		box().validate(reporter.makeChild(".box"));

	}

}
