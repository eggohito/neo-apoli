package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.fluid.FluidCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

import java.util.Optional;

public record FluidBlockCondition(FluidCondition fluidCondition) implements BlockCondition {

	public static final MapCodec<FluidBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(FluidCondition.CODEC.fieldOf("fluid_condition").forGetter(FluidBlockCondition::fluidCondition))
		.apply(instance, FluidBlockCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidBlockCondition> STREAM_CODEC = StreamCodec.composite(
		FluidCondition.STREAM_CODEC, FluidBlockCondition::fluidCondition,
		FluidBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.FLUID;
	}

	@Override
	public boolean test(Context context) {

		Level level = context.getLevel();
		Optional<FluidState> fluidState = context.optional(NeoApoliContextKeys.BLOCK_POS).map(level::getFluidState);

		Context fluidContext = new Context.Builder(context)
			.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.FLUID))
			.addOptional(NeoApoliContextKeys.FLUID_STATE, fluidState)
			.build(level);

		return fluidCondition().test(fluidContext.forChild(".fluid_condition"));

	}

	@Override
	public void validate(ProblemReporter reporter) {
		BlockCondition.super.validate(reporter);
		fluidCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.FLUID))
			.forChild(".fluid_condition"));
	}

}
