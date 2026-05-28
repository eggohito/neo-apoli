package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

public record EntityAttributeNumberProvider(Holder<Attribute> attribute, EntityProvider entity) implements NumberProvider {

	public static final MapCodec<EntityAttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(EntityAttributeNumberProvider::attribute),
		EntityProvider.CODEC.fieldOf("entity").forGetter(EntityAttributeNumberProvider::entity)
	).apply(instance, EntityAttributeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityAttributeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, EntityAttributeNumberProvider::attribute,
		EntityProvider.STREAM_CODEC, EntityAttributeNumberProvider::entity,
		EntityAttributeNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ENTITY_ATTRIBUTE;
	}

	@Override
	public double getDouble(Context context) {

		try {

			Entity entity = entity().getEntity(context.forChild(".entity")).orElse(null);
			ResourceLocation attributeLocation = attribute().unwrap().map(ResourceKey::location, BuiltInRegistries.ATTRIBUTE::getKey);

			switch (entity) {
				case LivingEntity livingEntity when context.visitor().push(this) -> {

					if (livingEntity.getAttributes().hasAttribute(attribute())) {
						return livingEntity.getAttributeValue(attribute());
					}

					else {
						context.forChild(".entity").reportProblem("Entity didn't have the attribute \"" + attributeLocation + "\"!");
					}

				}
				case LivingEntity ignored -> {
					//  No-op because this provider was recursively invoked
				}
				case null ->
					context.forChild(".entity").reportProblem("Entity doesn't exist!");
				default ->
					context.forChild(".entity").reportProblem("Entity can't have attributes!");
			}

			return 0.0D;

		}

		finally {
			context.visitor().pop(this);
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		entity().validate(validator.forChild(".entity"));
	}

}
