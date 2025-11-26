package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record AttributeNumberProvider(Holder<Attribute> attribute, TypedContextKey<Entity> entity) implements NumberProvider {

	public static final MapCodec<AttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(AttributeNumberProvider::attribute),
		NeoApoliCodecs.ENTITY_CONTEXT_KEY.fieldOf("entity").forGetter(AttributeNumberProvider::entity)
	).apply(instance, AttributeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttributeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, AttributeNumberProvider::attribute,
		NeoApoliStreamCodecs.ENTITY_CONTEXT_KEY, AttributeNumberProvider::entity,
		AttributeNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ATTRIBUTE;
	}

	@Override
	public @NotNull Number next(Context context) {

		ResourceLocation entityKeyLocation = entity().name();
		ResourceLocation attributeLocation = attribute().unwrap().map(ResourceKey::location, BuiltInRegistries.ATTRIBUTE::getKey);

		try {

			switch (context.nullable(entity())) {
				case LivingEntity livingEntity when context.markActive(this) -> {

					if (livingEntity.getAttributes().hasAttribute(attribute())) {
						return livingEntity.getAttributeValue(attribute());
					}

					else {
						context.getReporter().report("Entity from parameter \"" + entityKeyLocation + "\" doesn't have the attribute \"" + attributeLocation + "\"!");
					}

				}
				case LivingEntity ignored -> {
					//	No-op
				}
				case null ->
					context.getReporter().report("Couldn't get value of attribute \"" + attributeLocation + "\" from entity in parameter \"" + entityKeyLocation + "\", which doesn't exist!");
				default ->
					context.getReporter().report("Couldn't get value of attribute \"" + attributeLocation + "\" from entity in parameter \"" + entityKeyLocation + "\", as it isn't an entity that can have attributes!");
			}

			return 0.0D;

		}

		finally {
			context.markInActive(this);
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity());
	}

}
