package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.context.ContextParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record AttributeNumberProvider(RegistryEntry<EntityAttribute> attribute, EntityTarget entity) implements NumberProvider {

	public static final MapCodec<AttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityAttribute.CODEC.fieldOf("attribute").forGetter(AttributeNumberProvider::attribute),
		EntityTarget.CODEC.fieldOf("entity").forGetter(AttributeNumberProvider::entity)
	).apply(instance, AttributeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AttributeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityAttribute.PACKET_CODEC, AttributeNumberProvider::attribute,
		EntityTarget.PACKET_CODEC, AttributeNumberProvider::entity,
		AttributeNumberProvider::new
	);

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ATTRIBUTE;
	}

	@Override
	public @NotNull Number next(Context context) {

		ContextParameter<Entity> parameter = entity().getParameter();
		Context entityContext = context.makeChild(".entity");

		try {

			switch (context.nullable(parameter)) {
				case LivingEntity livingEntity -> {

					if (context.markActive(this)) {

						if (livingEntity.getAttributes().hasAttribute(this.attribute())) {
							return livingEntity.getAttributeValue(this.attribute());
						}

						else {
							entityContext.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" doesn't have the attribute \"" + this.attribute().getKeyOrValue().map(RegistryKey::getValue, Registries.ATTRIBUTE::getId) + "\"!");
						}

					}

				}
				case null ->
					entityContext.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" doesn't exist!");
				default ->
					entityContext.getReporter().report("Entity from parameter \"" + parameter.getId() + "\" is not an entity that can have attributes!");
			}

			return 0.0d;

		}

		finally {
			context.markInActive(this);
		}

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(entity().getParameter());
	}

}
