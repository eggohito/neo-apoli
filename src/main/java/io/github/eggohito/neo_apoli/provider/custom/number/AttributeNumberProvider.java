package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record AttributeNumberProvider(Holder<Attribute> attribute, EntityTarget entity) implements NumberProvider {

	public static final MapCodec<AttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(AttributeNumberProvider::attribute),
		EntityTarget.CODEC.fieldOf("entity").forGetter(AttributeNumberProvider::entity)
	).apply(instance, AttributeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, AttributeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, AttributeNumberProvider::attribute,
		EntityTarget.STREAM_CODEC, AttributeNumberProvider::entity,
		AttributeNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ATTRIBUTE;
	}

	@Override
	public @NotNull Number next(Context context) {

		ContextKey<Entity> parameter = entity().getParameter();
		Context entityContext = context.makeChild(".entity");

		try {

			switch (context.nullable(parameter)) {
				case LivingEntity livingEntity -> {

					if (context.markActive(this)) {

						if (livingEntity.getAttributes().hasAttribute(this.attribute())) {
							return livingEntity.getAttributeValue(this.attribute());
						}

						else {
							entityContext.getReporter().report("Entity from parameter \"" + parameter.name() + "\" doesn't have the attribute \"" + this.attribute().unwrap().map(ResourceKey::location, BuiltInRegistries.ATTRIBUTE::getKey) + "\"!");
						}

					}

				}
				case null ->
					entityContext.getReporter().report("Entity from parameter \"" + parameter.name() + "\" doesn't exist!");
				default ->
					entityContext.getReporter().report("Entity from parameter \"" + parameter.name() + "\" is not an entity that can have attributes!");
			}

			return 0.0d;

		}

		finally {
			context.markInActive(this);
		}

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
