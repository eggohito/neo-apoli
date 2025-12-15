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
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

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
	public void execute(Context context) {

		if (!(context.getLevel() instanceof ServerLevel serverLevel) || !context.hasParameter(NeoApoliContextKeys.BLOCK_POS)) {
			return;
		}

		Context radiusContext = context.forChild(".radius");
		int radius = radius().nextInt(radiusContext);

		if (radiusContext.hasErrors() || radius <= 0) {
			return;
		}

		BlockPos origin = context.required(NeoApoliContextKeys.BLOCK_POS);
		List<BlockPos> collectedPos = shape().getBlockPositions(origin, radius);

		for (var pos : collectedPos) {

			if (!serverLevel.hasChunkAt(pos)) {
				continue;
			}

			Context blockContext = new Context.Builder(context)
				.add(NeoApoliContextKeys.BLOCK_POS, pos)
				.add(NeoApoliContextKeys.BLOCK_STATE, serverLevel.getBlockState(pos))
				.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, serverLevel.getBlockEntity(pos))
				.build(serverLevel);

			if (blockCondition().test(blockContext.forChild(".block_condition"))) {
				blockAction().execute(blockContext.forChild(".block_action"));
			}

		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		BlockAction.super.validate(reporter);
		blockAction().validate(reporter.forChild(".block_action"));
		blockCondition().validate(reporter.forChild(".block_condition"));
		radius().validate(reporter.forChild(".radius"));
	}

}
