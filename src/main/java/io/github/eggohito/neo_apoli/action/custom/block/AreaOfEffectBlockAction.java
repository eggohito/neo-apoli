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
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public record AreaOfEffectBlockAction(BlockAction blockAction, BlockCondition blockCondition, Shape shape, NumberProvider radius) implements BlockAction {

	public static final MapCodec<AreaOfEffectBlockAction> CODEC = MapCodecUtil.lazy(AreaOfEffectBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockAction.CODEC.fieldOf("block_action").forGetter(AreaOfEffectBlockAction::blockAction),
		BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(AreaOfEffectBlockAction::blockCondition),
		Shape.CODEC.optionalFieldOf("shape", Shape.CUBE).forGetter(AreaOfEffectBlockAction::shape),
		NumberProvider.CODEC.fieldOf("radius").forGetter(AreaOfEffectBlockAction::radius)
	).apply(instance, AreaOfEffectBlockAction::new)));

	public static final StreamCodec<RegistryFriendlyByteBuf, AreaOfEffectBlockAction> STREAM_CODEC = StreamCodec.composite(
		BlockAction.STREAM_CODEC, AreaOfEffectBlockAction::blockAction,
		BlockCondition.STREAM_CODEC, AreaOfEffectBlockAction::blockCondition,
		Shape.STREAM_CODEC, AreaOfEffectBlockAction::shape,
		NumberProvider.STREAM_CODEC, AreaOfEffectBlockAction::radius,
		AreaOfEffectBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.AREA_OF_EFFECT;
	}

	@Override
	public void serverExecute(ServerContext context) {

		ServerLevel world = context.getWorld();
		BlockPos originBlockPos = context.nullable(NeoApoliContextKeys.BLOCK_POS);

		if (originBlockPos == null) {
			return;
		}

		ServerContext radiusContext = context.makeChild(".radius");
		int radius = radius().nextInt(radiusContext);

		if (radiusContext.hasErrors() || radius <= 0) {
			return;
		}

		for (BlockPos blockPos : shape().getBlockPositions(originBlockPos, radius)) {

			if (!world.hasChunkAt(blockPos)) {
				continue;
			}

			ServerContext blockContext = new ServerContext.Builder(context)
				.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
				.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(blockPos))
				.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(blockPos))
				.build(world);

			if (blockCondition().test(blockContext.makeChild(".block_condition"))) {
				blockAction().execute(blockContext.makeChild(".block_action"));
			}

		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_POS);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		BlockAction.super.validate(reporter);
		blockAction().validate(reporter.forChild(".block_action"));
		blockCondition().validate(reporter.forChild(".block_condition"));
		radius().validate(reporter.forChild(".radius"));
	}

}
