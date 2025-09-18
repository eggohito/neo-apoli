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
import io.github.eggohito.neo_apoli.util.context.ContextTypeUtil;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;

@EqualsAndHashCode
@Data
public final class BlocksInRadiusNumberProvider extends NumberProvider {

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

	private final BlockCondition blockCondition;

	private final Shape shape;
	private final NumberProvider radius;

	public BlocksInRadiusNumberProvider(BlockCondition blockCondition, Shape shape, NumberProvider radius) {
		this.blockCondition = blockCondition;
		this.shape = shape;
		this.radius = radius;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_IN_RADIUS;
	}

	@Override
	protected Number impl(Context context) {

		Context radiusContext = context.makeChild(".radius");
		int radius = this.radius().nextInt(radiusContext);

		if (radiusContext.hasErrors()) {
			return 0.0;
		}

		World world = context.getWorld();
		BlockPos centerPos = BlockPos.ofFloored(context.required(ContextParameters.POSITION));

		int matches = 0;

		for (BlockPos pos : this.shape().getBlockPositions(centerPos, radius)) {

			if (!world.isChunkLoaded(pos)) {
				continue;
			}

			Context blockContext = context.copy(builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
				.add(ContextParameters.BLOCK_POS, pos)
				.add(ContextParameters.BLOCK_STATE, world.getBlockState(pos))
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos)));

			if (this.blockCondition().test(blockContext.makeChild(".block_condition"))) {
				matches++;
			}

		}

		return matches;

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		blockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".block_condition"));
		radius().validate(reporter.makeChild(".radius"));

	}

}
