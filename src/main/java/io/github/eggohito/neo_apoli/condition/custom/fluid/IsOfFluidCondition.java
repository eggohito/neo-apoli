package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

public record IsOfFluidCondition(Fluid fluid) implements FluidCondition {

	public static final MapCodec<IsOfFluidCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(IsOfFluidCondition::fluid))
		.apply(instance, IsOfFluidCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsOfFluidCondition> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.FLUID), IsOfFluidCondition::fluid,
		IsOfFluidCondition::new
	);

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.IS_OF;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.FLUID_STATE)
			.map(state -> state.is(fluid()))
			.orElse(false);
	}

}
