package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Set;

public record AdjacentBlocksNumberProvider(BlockCondition adjacentBlockCondition) implements NumberProvider {

	public static final MapCodec<AdjacentBlocksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("adjacent_block_condition", new ConstantBlockCondition(true)).forGetter(AdjacentBlocksNumberProvider::adjacentBlockCondition)
	).apply(instance, AdjacentBlocksNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AdjacentBlocksNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, AdjacentBlocksNumberProvider::adjacentBlockCondition,
		AdjacentBlocksNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ADJACENT_BLOCKS;
	}

	@Override
	public double doubleValue(Context context) {

		World world = context.getWorld();
		BlockPos pos = BlockPos.ofFloored(context.requiredParameter(ContextParameters.POSITION));

		long matches = 0;
		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = pos.offset(direction);
			Context offsetContext = context.copy(builder -> builder.add(ContextParameters.POSITION, offsetPos.toCenterPos()));

			if (world.isChunkLoaded(offsetPos) && adjacentBlockCondition().test(offsetContext)) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter);
		adjacentBlockCondition().validate(reporter.makeChild("adjacent_block_condition"));
	}

}
