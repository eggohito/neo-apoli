package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record FluidHeightFromEntityNumberProvider(TagKey<Fluid> fluidTag, ContextParameter<Entity> entity) implements NumberProvider {

	public static final MapCodec<FluidHeightFromEntityNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.hashedCodec(Registries.FLUID).fieldOf("fluid_tag").forGetter(FluidHeightFromEntityNumberProvider::fluidTag),
		NeoApoliContextParams.Codecs.ENTITY.fieldOf("entity").forGetter(FluidHeightFromEntityNumberProvider::entity)
	).apply(instance, FluidHeightFromEntityNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidHeightFromEntityNumberProvider> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.FLUID), FluidHeightFromEntityNumberProvider::fluidTag,
		NeoApoliContextParams.StreamCodecs.ENTITY, FluidHeightFromEntityNumberProvider::entity,
		FluidHeightFromEntityNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.FLUID_HEIGHT_FROM_ENTITY;
	}

	@Override
	public double nextDouble(Context context) {
		return context.getOptional(entity())
			.map(entity -> entity.getFluidHeight(this.fluidTag()))
			.orElse(0.0D);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".fluid_tag"), this.fluidTag());
	}

}
