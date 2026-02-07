package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MapCodecUtil;
import io.github.eggohito.neo_apoli.util.Shape;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

public record AreaOfEffectBlockAction(BlockAction blockAction, BlockCondition blockCondition, Shape shape, NumberProvider radius) implements BlockAction {

	public static final MapCodec<AreaOfEffectBlockAction> MAP_CODEC = MapCodecUtil.lazy(AreaOfEffectBlockAction.class.getSimpleName(), () -> RecordCodecBuilder.mapCodec(instance -> instance.group(
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

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		BlockPos originBlockPos = context.getRequired(NeoApoliContextParams.BLOCK_POS);
		int radius = radius().nextInt(context.forChild(".radius"));

		for (var blockPos : shape().getBlockPositions(originBlockPos, radius)) {

			if (!serverLevel.hasChunkAt(blockPos)) {
				continue;
			}

			Context blockContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, serverLevel.getBlockState(blockPos))
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, serverLevel.getBlockEntity(blockPos))
				.build(serverLevel);

			if (blockCondition().test(blockContext.forChild(".block_condition"))) {
				blockAction().execute(blockContext.forChild(".block_action"));
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		BlockAction.super.validate(validator);
		blockAction().validate(validator.forChild(".block_action"));
		blockCondition().validate(validator.forChild(".block_condition"));
		radius().validate(validator.forChild(".radius"));
	}

}
