package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record IsOnBlockEntityCondition(BlockCondition blockCondition) implements EntityCondition {

	private static final ContextKeySet CONDITION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<IsOnBlockEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockCondition.CODEC.optionalFieldOf("block_condition", new ConstantBlockCondition(true)).forGetter(IsOnBlockEntityCondition::blockCondition))
		.apply(instance, IsOnBlockEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOnBlockEntityCondition> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, IsOnBlockEntityCondition::blockCondition,
		IsOnBlockEntityCondition::new
	);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.IS_ON_BLOCK;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Level level = context.level();
		Entity entity = context.getRequired(NeoApoliContextParams.THIS_ENTITY);

		try {

			if (context.visitor().push(this)) {

				if (!entity.onGround()) {
					return false;
				}

				BlockPos steppingPos = entity.getOnPos();
				Context conditionContext = new Context.Builder(context)
					.withRequired(NeoApoliContextParams.BLOCK_POS, steppingPos)
					.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(steppingPos))
					.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(steppingPos))
					.build(level);

				return blockCondition().test(conditionContext);

			}

			else {
				return false;
			}

		}

		finally {
			context.visitor().pop(this);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		blockCondition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMS).forChild(".block_condition"));
	}

}
