package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliFluidConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public record IsInTagFluidCondition(TagKey<Fluid> tag) implements FluidCondition {

	public static final MapCodec<IsInTagFluidCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(TagKey.hashedCodec(Registries.FLUID).fieldOf("tag").forGetter(IsInTagFluidCondition::tag))
		.apply(instance, IsInTagFluidCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagFluidCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.FLUID), IsInTagFluidCondition::tag,
		IsInTagFluidCondition::new
	);

	@Override
	public FluidCondition.Type<?> getType() {
		return NeoApoliFluidConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.FLUID_STATE)
			.map(state -> state.is(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(Context.Validator validator) {
		FluidCondition.super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".tag"), this.tag());
	}

}
