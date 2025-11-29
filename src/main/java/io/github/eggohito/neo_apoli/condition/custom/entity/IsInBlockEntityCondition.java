package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.Level;

import java.util.Set;

public record IsInBlockEntityCondition(BlockCondition condition) implements EntityCondition {

	public static final MapCodec<IsInBlockEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BlockCondition.CODEC.fieldOf("block_condition").forGetter(IsInBlockEntityCondition::condition))
		.apply(instance, IsInBlockEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInBlockEntityCondition> STREAM_CODEC = StreamCodec.composite(
		BlockCondition.STREAM_CODEC, IsInBlockEntityCondition::condition,
		IsInBlockEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.IS_IN_BLOCK;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Level world = context.getWorld();
		BlockPos blockPos = BlockPos.containing(context.required(NeoApoliContextKeys.THIS_POS));

		if (!world.hasChunkAt(blockPos)) {
			return false;
		}

		Context blockContext = ContextImpl.of(context, builder -> builder
			.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, world.getBlockState(blockPos))
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

		return condition().test(blockContext.makeChild(".block_condition"));

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.THIS_POS);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		EntityCondition.super.validate(reporter);
		condition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.forChild(".block_condition"));
	}

}
