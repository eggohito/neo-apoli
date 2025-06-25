package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

@EqualsAndHashCode(callSuper = false)
@Data
public final class AttributeNumberProvider extends NumberProvider {

	public static final MapCodec<AttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityParameter.CODEC.fieldOf("source").forGetter(AttributeNumberProvider::source),
		Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeNumberProvider::attribute)
	).apply(instance, AttributeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AttributeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		EntityParameter.PACKET_CODEC, AttributeNumberProvider::source,
		EntityAttribute.PACKET_CODEC, AttributeNumberProvider::attribute,
		AttributeNumberProvider::new
	);

	private final EntityParameter source;
	private final RegistryEntry<EntityAttribute> attribute;

	public AttributeNumberProvider(EntityParameter source, RegistryEntry<EntityAttribute> attribute) {
		this.source = source;
		this.attribute = attribute;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.ATTRIBUTE;
	}

	@Override
	protected double doubleImpl(Context context) {

		ContextParameter<? extends Entity> source = source().getParameter();
		Context sourceContext = context.makeChild(".source");

		return switch (context.nullable(source)) {
			case LivingEntity livingEntity -> {

				if (livingEntity.getAttributes().hasAttribute(this.attribute())) {
					yield livingEntity.getAttributeValue(this.attribute());
				}

				else {
					sourceContext.getReporter().report("Entity from parameter \"" + source.getId() + "\" doesn't have attribute \"" + this.attribute().getKeyOrValue().map(RegistryKey::getValue, Registries.ATTRIBUTE::getId) + "\"!");
					yield 0.0D;
				}

			}
			case null -> {
				sourceContext.getReporter().report("Entity from parameter \"" + source.getId() + "\" doesn't exist!");
				yield 0.0;
			}
			default -> {
				sourceContext.getReporter().report("Entity from parameter \"" + source.getId() + "\" is not a living entity!");
				yield 0.0D;
			}
		};

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

}
