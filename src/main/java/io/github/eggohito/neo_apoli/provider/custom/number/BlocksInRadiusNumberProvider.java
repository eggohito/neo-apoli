package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.PacketCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public record BlocksInRadiusNumberProvider(BlockCondition blockCondition, Vec3dProvider position, Shape shape, NumberProvider radius) implements NumberProvider {

	public static final MapCodec<BlocksInRadiusNumberProvider> CODEC = MapCodecUtil.lazy(BlocksInRadiusNumberProvider.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockCondition.CODEC.fieldOf("block_condition").forGetter(BlocksInRadiusNumberProvider::blockCondition),
		Vec3dProvider.CODEC.fieldOf("position").forGetter(BlocksInRadiusNumberProvider::position),
		Shape.CODEC.fieldOf("shape").forGetter(BlocksInRadiusNumberProvider::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(BlocksInRadiusNumberProvider::radius)
	).apply(instance, BlocksInRadiusNumberProvider::new)));

	public static final PacketCodec<RegistryByteBuf, BlocksInRadiusNumberProvider> PACKET_CODEC = PacketCodecUtil.lazy(BlocksInRadiusNumberProvider.class.getSimpleName(), () -> PacketCodec.tuple(
		BlockCondition.PACKET_CODEC, BlocksInRadiusNumberProvider::blockCondition,
		Vec3dProvider.PACKET_CODEC, BlocksInRadiusNumberProvider::position,
		Shape.PACKET_CODEC, BlocksInRadiusNumberProvider::shape,
		NumberProvider.PACKET_CODEC, BlocksInRadiusNumberProvider::radius,
		BlocksInRadiusNumberProvider::new
	));

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.BLOCKS_IN_RADIUS;
	}

	@Override
	public @NotNull Number next(Context context) {

		World world = context.getWorld();
		int matches = 0;

		Context positionContext = context.makeChild(".position");
		Vec3d position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return matches;
		}

		Context radiusContext = context.makeChild(".radius");
		int radius = radius().nextInt(radiusContext);

		if (radiusContext.hasErrors()) {
			return matches;
		}

		for (var blockPos : shape().getBlockPositions(BlockPos.ofFloored(position), radius)) {

			if (!world.isChunkLoaded(blockPos)) {
				continue;
			}

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

		position().validate(reporter.makeChild(".position"));
		radius().validate(reporter.makeChild(".radius"));

	}

}
