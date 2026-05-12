package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliEntityConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;

import java.util.Set;

public record IsInBlockEntityCondition(BlockCondition condition) implements EntityCondition {

	private static final ContextKeySet CONDITION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	public static final MapCodec<IsInBlockEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockCondition.CODEC.fieldOf("block_condition").forGetter(IsInBlockEntityCondition::condition))
		.apply(instance, IsInBlockEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInBlockEntityCondition> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, IsInBlockEntityCondition::condition,
		IsInBlockEntityCondition::new
	);

	@Override
	public EntityCondition.Type<?> getType() {
		return NeoApoliEntityConditionTypes.IS_IN_BLOCK;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Level level = context.level();
		BlockPos blockPos = BlockPos.containing(context.getRequired(NeoApoliContextParams.THIS_POS));

		if (!level.hasChunkAt(blockPos)) {
			return false;
		}

		Context conditionContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
			.withRequired(NeoApoliContextParams.BLOCK_STATE, level.getBlockState(blockPos))
			.withNullable(NeoApoliContextParams.BLOCK_ENTITY, level.getBlockEntity(blockPos))
			.build(level);

		return condition().test(conditionContext.forChild(".block_condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.THIS_POS);
	}

	@Override
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		condition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMS).forChild(".block_condition"));
	}

}
