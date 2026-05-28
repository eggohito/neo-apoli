package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public record EntityFluidHeightNumberProvider(TagKey<Fluid> fluidTag, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<EntityFluidHeightNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		TagKey.hashedCodec(Registries.FLUID).fieldOf("fluid_tag").forGetter(EntityFluidHeightNumberProvider::fluidTag),
		EntityProvider.CODEC.fieldOf("entity").forGetter(EntityFluidHeightNumberProvider::entity)
	).apply(instance, EntityFluidHeightNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityFluidHeightNumberProvider> STREAM_CODEC = StreamCodec.composite(
		TagKey.streamCodec(Registries.FLUID), EntityFluidHeightNumberProvider::fluidTag,
		EntityProvider.STREAM_CODEC, EntityFluidHeightNumberProvider::entity,
		EntityFluidHeightNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ENTITY_FLUID_HEIGHT;
	}

	@Override
	public double getDouble(Context context) {
		return entity().getEntity(context.forChild(".entity"))
			.map(entity -> entity.getFluidHeight(this.fluidTag()))
			.orElse(0.0D);
	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		RegistryUtil.validateTag(validator.forChild(".fluid_tag"), fluidTag());
		entity().validate(validator.forChild(".entity"));
	}

}
