package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;

import java.util.Set;

public record AreaOfEffectBlockAction(BlockAction blockAction, BlockCondition blockCondition, Shape shape, NumberProvider radius) implements BlockAction {

	public static final MapCodec<AreaOfEffectBlockAction> CODEC = MapCodecUtil.lazy(AreaOfEffectBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("block_action").forGetter(AreaOfEffectBlockAction::blockAction),
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(AreaOfEffectBlockAction::blockCondition),
		Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(AreaOfEffectBlockAction::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(AreaOfEffectBlockAction::radius)
	).apply(instance, AreaOfEffectBlockAction::new)));

	public static final PacketCodec<RegistryByteBuf, AreaOfEffectBlockAction> PACKET_CODEC = PacketCodec.tuple(
		BlockAction.PACKET_CODEC, AreaOfEffectBlockAction::blockAction,
		BlockCondition.PACKET_CODEC, AreaOfEffectBlockAction::blockCondition,
		Shape.PACKET_CODEC, AreaOfEffectBlockAction::shape,
		NumberProvider.PACKET_CODEC, AreaOfEffectBlockAction::radius,
		AreaOfEffectBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.AREA_OF_EFFECT;
	}

	@Override
	public void serverExecute(ServerContext context) {

		ServerWorld world = context.getWorld();
		BlockPos originBlockPos = context.nullable(ContextParameters.BLOCK_POS);

		if (originBlockPos == null) {
			return;
		}

		ServerContext radiusContext = context.makeChild(".radius");
		int radius = radius().nextInt(radiusContext);

		if (radiusContext.hasErrors() || radius <= 0) {
			return;
		}

		for (BlockPos blockPos : shape().getBlockPositions(originBlockPos, radius)) {

			if (!world.isChunkLoaded(blockPos)) {
				continue;
			}

			ServerContext blockContext = new ServerContext.Builder(context)
				.add(ContextParameters.BLOCK_POS, blockPos)
				.add(ContextParameters.BLOCK_STATE, world.getBlockState(blockPos))
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos))
				.build(world);

			if (blockCondition().test(blockContext.makeChild(".block_condition"))) {
				blockAction().execute(blockContext.makeChild(".block_action"));
			}

		}

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(ContextParameters.BLOCK_POS);
	}

	@Override
	public void validate(ErrorReporter reporter) {
		BlockAction.super.validate(reporter);
		blockAction().validate(reporter.makeChild(".block_action"));
		blockCondition().validate(reporter.makeChild(".block_condition"));
		radius().validate(reporter.makeChild(".radius"));
	}

}
