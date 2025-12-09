package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;

import java.util.Set;

public interface FluidCondition extends Condition {

	Codec<FluidCondition> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(FluidConditionType.CODEC.dispatch(FluidCondition::getType, FluidConditionType::mapCodec), ConstantFluidCondition.INLINE_CODEC));

	StreamCodec<RegistryFriendlyByteBuf, FluidCondition> STREAM_CODEC = FluidConditionType.STREAM_CODEC.dispatch(FluidCondition::getType, FluidConditionType::streamCodec);

	@Override
	FluidConditionType<?> getType();

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.FLUID_STATE);
	}

	@Override
	default String asDisplayString() {
		return "Fluid condition with type \"" + RegistryUtil.getId(NeoApoliRegistries.FLUID_CONDITION_TYPE, this.getType()) + "\"";
	}

}
