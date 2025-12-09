package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record FluidHeightFromEntityNumberProvider(TagKey<Fluid> fluidTag, TypedContextKey<Entity> entity) implements NumberProvider {

	public static final MapCodec<FluidHeightFromEntityNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		CodecUtil.hashedTag(Registries.FLUID).fieldOf("fluid_tag").forGetter(FluidHeightFromEntityNumberProvider::fluidTag),
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(FluidHeightFromEntityNumberProvider::entity)
	).apply(instance, FluidHeightFromEntityNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidHeightFromEntityNumberProvider> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.FLUID), FluidHeightFromEntityNumberProvider::fluidTag,
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, FluidHeightFromEntityNumberProvider::entity,
		FluidHeightFromEntityNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.FLUID_HEIGHT_FROM_ENTITY;
	}

	@Override
	public @NotNull Number next(Context context) {
		return context.optional(entity())
			.map(entity -> entity.getFluidHeight(this.fluidTag()))
			.orElse(0.0D);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public void validate(ProblemReporter reporter) {
		NumberProvider.super.validate(reporter);
		RegistryUtil.validateTag(reporter.forChild(".fluid_tag"), this.fluidTag());
	}

}
