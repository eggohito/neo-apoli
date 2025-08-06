package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public record AttributeModifier(RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {

	public static final Codec<AttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Registries.ATTRIBUTE.getEntryCodec().fieldOf("attribute").forGetter(AttributeModifier::attribute),
		EntityAttributeModifier.MAP_CODEC.forGetter(AttributeModifier::modifier)
	).apply(instance, AttributeModifier::new));

	public static final PacketCodec<RegistryByteBuf, AttributeModifier> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.registryEntry(RegistryKeys.ATTRIBUTE), AttributeModifier::attribute,
		EntityAttributeModifier.PACKET_CODEC, AttributeModifier::modifier,
		AttributeModifier::new
	);

}
