package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record AttributeNumberProvider(LootContext.EntityTarget source, RegistryEntry<EntityAttribute> attribute, NumberProvider scale) implements NumberProvider {

	public static final MapCodec<AttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.fieldOf("source").forGetter(AttributeNumberProvider::source),
		Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeNumberProvider::attribute),
		NumberProvider.CODEC.optionalFieldOf("scale", new ConstantNumberProvider(1.0D)).forGetter(AttributeNumberProvider::scale)
	).apply(instance, AttributeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AttributeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.ENTITY_TARGET, AttributeNumberProvider::source,
		EntityAttribute.PACKET_CODEC, AttributeNumberProvider::attribute,
		NumberProvider.PACKET_CODEC, AttributeNumberProvider::scale,
		AttributeNumberProvider::new
	);

	@Override
	public Number get(ErrorReporter reporter, Context context) {

		if (context.nullableParameter(source().getParameter()) instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(attribute())) {
			return livingEntity.getAttributeValue(attribute()) * scale().get(reporter, context).doubleValue();
		}

		else {
			return 0.0D;
		}

	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.ATTRIBUTE;
	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

	@Override
	public void validate(ErrorReporter reporter) {
		NumberProvider.super.validate(reporter.makeChild("source"));
		scale().validate(reporter.makeChild("scale"));
	}

}
