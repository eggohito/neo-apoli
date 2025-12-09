package io.github.eggohito.neo_apoli.condition.custom.fluid;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionType;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public record IsInTagFluidCondition(TagKey<Fluid> tag) implements FluidCondition {

	public static final MapCodec<IsInTagFluidCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(CodecUtil.hashedTag(Registries.FLUID).fieldOf("tag").forGetter(IsInTagFluidCondition::tag))
		.apply(instance, IsInTagFluidCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IsInTagFluidCondition> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.FLUID), IsInTagFluidCondition::tag,
		IsInTagFluidCondition::new
	);

	@Override
	public FluidConditionType<?> getType() {
		return FluidConditionTypes.IS_IN_TAG;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.FLUID_STATE)
			.map(state -> state.is(this.tag()))
			.orElse(false);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		FluidCondition.super.validate(reporter);
		RegistryUtil.validateTag(reporter.forChild(".tag"), this.tag());
	}

}
