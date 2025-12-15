package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record IsOnBlockEntityCondition(BlockCondition blockCondition) implements EntityCondition {

	public static final MapCodec<IsOnBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(IsOnBlockEntityCondition::blockCondition))
		.apply(instance, IsOnBlockEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOnBlockEntityCondition> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, IsOnBlockEntityCondition::blockCondition,
		IsOnBlockEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_ON_BLOCK;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Level level = context.getLevel();
		Entity entity = context.required(NeoApoliContextKeys.THIS_ENTITY);

		try {

			if (context.markActive(this)) {

				if (!entity.onGround()) {
					return false;
				}

				BlockPos steppingPos = entity.getOnPos();
				Context blockContext = new Context.Builder(context)
					.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
					.add(NeoApoliContextKeys.BLOCK_POS, steppingPos)
					.add(NeoApoliContextKeys.BLOCK_STATE, level.getBlockState(steppingPos))
					.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, level.getBlockEntity(steppingPos))
					.build(level);

				return blockCondition().test(blockContext.forChild(".block_condition"));

			}

			else {
				return false;
			}

		}

		finally {
			context.markInActive(this);
		}

	}

	@Override
	public void validate(ProblemReporter reporter) {
		EntityCondition.super.validate(reporter);
		blockCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.forChild(".block_condition"));
	}

}
