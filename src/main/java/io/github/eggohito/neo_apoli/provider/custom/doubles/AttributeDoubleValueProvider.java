package io.github.eggohito.neo_apoli.provider.custom.doubles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.provider.DoubleValueProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderType;
import io.github.eggohito.neo_apoli.provider.type.doubles.DoubleValueProviderTypes;
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

public record AttributeDoubleValueProvider(LootContext.EntityTarget source, RegistryEntry<EntityAttribute> attribute, double scale) implements DoubleValueProvider {

	public static final MapCodec<AttributeDoubleValueProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		LootContext.EntityTarget.CODEC.fieldOf("source").forGetter(AttributeDoubleValueProvider::source),
		Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeDoubleValueProvider::attribute),
		Codec.DOUBLE.optionalFieldOf("scale", 1.0D).forGetter(AttributeDoubleValueProvider::scale)
	).apply(instance, AttributeDoubleValueProvider::new));

	public static final PacketCodec<RegistryByteBuf, AttributeDoubleValueProvider> PACKET_CODEC = PacketCodec.tuple(
		NeoApoliPacketCodecs.ENTITY_TARGET, AttributeDoubleValueProvider::source,
		EntityAttribute.PACKET_CODEC, AttributeDoubleValueProvider::attribute,
		PacketCodecs.DOUBLE, AttributeDoubleValueProvider::scale,
		AttributeDoubleValueProvider::new
	);

	@Override
	public DoubleValueProviderType<?> getType() {
		return DoubleValueProviderTypes.ATTRIBUTE;
	}

	@Override
	public double getDouble(ValueProviderContext context) {

		if (context.requireParameter(source().getParameter()) instanceof LivingEntity livingEntity && livingEntity.getAttributes().hasAttribute(attribute)) {
			return livingEntity.getAttributeValue(attribute) * scale();
		}

		else {
			return 0;
		}

	}

	@Override
	public Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(source().getParameter());
	}

}
