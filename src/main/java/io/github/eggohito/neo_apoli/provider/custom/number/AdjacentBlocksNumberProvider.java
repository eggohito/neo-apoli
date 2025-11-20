package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record AdjacentBlocksNumberProvider(BlockCondition adjacentBlockCondition, Vec3dProvider position) implements NumberProvider {

	public static final MapCodec<AdjacentBlocksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(AdjacentBlocksNumberProvider::adjacentBlockCondition),
		Vec3dProvider.CODEC.fieldOf("position").forGetter(AdjacentBlocksNumberProvider::position)
	).apply(instance, AdjacentBlocksNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AdjacentBlocksNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, AdjacentBlocksNumberProvider::adjacentBlockCondition,
		Vec3dProvider.PACKET_CODEC, AdjacentBlocksNumberProvider::position,
		AdjacentBlocksNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ADJACENT_BLOCKS_NUMBER_PROVIDER;
	}

	@Override
	public @NotNull Number next(Context context) {

		World world = context.getWorld();
		long matches = 0;

		Context positionContext = context.makeChild(".position");
		BlockPos blockPos = BlockPos.ofFloored(position().next(positionContext));

		if (positionContext.hasErrors()) {
			return matches;
		}

		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = blockPos.offset(direction);

			if (!world.isChunkLoaded(offsetPos)) {
				continue;
			}

			Context blockContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), NeoApoliContextTypes.BLOCK))
				.add(NeoApoliContextParameters.BLOCK_POS, offsetPos)
				.add(NeoApoliContextParameters.BLOCK_STATE, world.getBlockState(offsetPos))
				.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(offsetPos)));

			if (adjacentBlockCondition().test(blockContext.makeChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public void validate(ErrorReporter reporter) {

		NumberProvider.super.validate(reporter);
		adjacentBlockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), NeoApoliContextTypes.BLOCK))
			.makeChild(".block_condition"));

		position().validate(reporter.makeChild(".position"));

	}

}
