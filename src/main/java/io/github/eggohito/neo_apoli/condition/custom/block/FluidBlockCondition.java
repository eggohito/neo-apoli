package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.fluid.FluidCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public record FluidBlockCondition(FluidCondition fluidCondition) implements BlockCondition {

	private static final ContextKeySet CONDITION_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.FLUID_STATE)
		.build();

	public static final MapCodec<FluidBlockCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
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

		if (!context.hasAllParameters(this.getRequiredParameters())) {
			return false;
		}

		Level level = context.level();
		FluidState fluidState = context.getRequired(NeoApoliContextParams.BLOCK_STATE).getFluidState();

		Context conditionContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.FLUID_STATE, fluidState)
			.build(level);

		return fluidCondition().test(conditionContext.forChild(".fluid_condition"));

	}

	@Override
	public void validate(Context.Validator validator) {
		BlockCondition.super.validate(validator);
		fluidCondition().validate(validator.withAdditionalKeysFromSets(CONDITION_PARAMS).forChild(".fluid_condition"));
	}

}
