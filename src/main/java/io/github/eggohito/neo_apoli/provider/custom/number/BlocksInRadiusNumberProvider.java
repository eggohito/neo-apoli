package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

public record BlocksInRadiusNumberProvider(BlockCondition blockCondition, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<BlocksInRadiusNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(BlocksInRadiusNumberProvider::blockCondition),
		Shape.CODEC.fieldOf("shape").forGetter(BlocksInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(BlocksInRadiusNumberProvider::radius)
	).apply(instance, BlocksInRadiusNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, BlocksInRadiusNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, BlocksInRadiusNumberProvider::blockCondition,
		Shape.PACKET_CODEC, BlocksInRadiusNumberProvider::shape,
		NumberProvider.PACKET_CODEC, BlocksInRadiusNumberProvider::radius,
		BlocksInRadiusNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_IN_RADIUS;
	}

	@Override
	public double doubleValue(Context context) {

		Context radiusContext = context.makeChild("radius");
		int radius = this.radius().intValue(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0.0;
		}

		World world = context.getWorld();
		BlockPos centerPos = BlockPos.ofFloored(context.required(ContextParameters.POSITION));

		Context.Builder builder = new Context.Builder(context);
		int matches = 0;

		for (BlockPos pos : this.shape().getBlockPositions(centerPos, radius)) {

			if (!world.isChunkLoaded(pos)) {
				continue;
			}

			Context blockConditionContext = builder
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos))
				.add(ContextParameters.BLOCK_STATE, world.getBlockState(pos))
				.add(ContextParameters.POSITION, pos.toCenterPos())
				.build(context.getWorld())
				.makeChild("block_condition");

			if (this.blockCondition().test(blockConditionContext)) {
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

		blockCondition().validate(reporter.makeChild("block_condition"));
		radius().validate(reporter.makeChild("radius"));

	}

}
