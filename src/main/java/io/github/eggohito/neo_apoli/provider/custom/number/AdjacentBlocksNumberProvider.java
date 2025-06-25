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
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
public final class AdjacentBlocksNumberProvider extends NumberProvider {

	public static final MapCodec<AdjacentBlocksNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("adjacent_block_condition", new ConstantBlockCondition(true)).forGetter(AdjacentBlocksNumberProvider::adjacentBlockCondition)
	).apply(instance, AdjacentBlocksNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AdjacentBlocksNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, AdjacentBlocksNumberProvider::adjacentBlockCondition,
		AdjacentBlocksNumberProvider::new
	);

	private final BlockCondition adjacentBlockCondition;

	public AdjacentBlocksNumberProvider(BlockCondition adjacentBlockCondition) {
		this.adjacentBlockCondition = adjacentBlockCondition;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ADJACENT_BLOCKS;
	}

	@Override
	protected double doubleImpl(Context context) {

		World world = context.getWorld();
		BlockPos pos = BlockPos.ofFloored(context.required(ContextParameters.POSITION));

		long matches = 0;
		for (Direction direction : Direction.values()) {

			BlockPos offsetPos = pos.offset(direction);
			Context blockContext = context
				.copy(builder -> builder.add(ContextParameters.POSITION, offsetPos.toCenterPos()))
				.makeChild(".adjacent_block_condition");

			if (world.isChunkLoaded(offsetPos) && adjacentBlockCondition().test(blockContext)) {
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
		super.validate(reporter);
		adjacentBlockCondition().validate(reporter.makeChild(".adjacent_block_condition"));
	}

}
