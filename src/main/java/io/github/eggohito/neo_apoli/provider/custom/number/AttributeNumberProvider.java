package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.context.ContextParameter;

import java.util.Set;

public record AttributeNumberProvider(LootContext.EntityTarget source, RegistryEntry<EntityAttribute> attribute, double scale) implements NumberProvider {

	public static final MapCodec<AttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.fieldOf("source").forGetter(AttributeNumberProvider::source),
		Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeNumberProvider::attribute),
		Codec.DOUBLE.optionalFieldOf("scale", 1.0D).forGetter(AttributeNumberProvider::scale)
	).apply(instance, AttributeNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, AttributeNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.ENTITY_TARGET, AttributeNumberProvider::source,
		EntityAttribute.PACKET_CODEC, AttributeNumberProvider::attribute,
		PacketCodecs.DOUBLE, AttributeNumberProvider::scale,
		AttributeNumberProvider::new
	);

	@Override
	public Number get(ValueProviderContext context) {

		if (context.requireParameter(source().getParameter()) instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(attribute())) {
			return livingEntity.getAttributeValue(attribute()) * scale();
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

}
