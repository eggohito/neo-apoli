package io.github.eggohito.neo_apoli.provider.custom.number;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ItemAttributeNumberProvider(Holder<Attribute> attribute, Optional<ContextParameter<Entity>> entity) implements NumberProvider {

	public static final MapCodec<ItemAttributeNumberProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(ItemAttributeNumberProvider::attribute),
		NeoApoliContextParams.Codecs.ENTITY.optionalFieldOf("entity").forGetter(ItemAttributeNumberProvider::entity)
	).apply(instance, ItemAttributeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, ItemAttributeNumberProvider::attribute,
		ByteBufCodecs.optional(NeoApoliContextParams.StreamCodecs.ENTITY), ItemAttributeNumberProvider::entity,
		ItemAttributeNumberProvider::new
	);

	@Override
	public @NotNull NumberProviderType<?> getType() {
		return NumberProviderTypes.ITEM_ATTRIBUTE;
	}

	@Override
	public @NotNull Number nextNumber(Context context) {
		return context.getOptional(NeoApoliContextParams.ITEM_STACK)
			.map(stack -> stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY))
			.map(modifiers -> this.compute(context, modifiers))
			.orElse(0.0d);
	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {

		ImmutableSet.Builder<ContextKey<?>> requirements = ImmutableSet.builder();

		requirements.add(NeoApoliContextParams.ITEM_STACK);
		entity().ifPresent(requirements::add);

		return requirements.build();

	}

	private double compute(Context context, ItemAttributeModifiers modifiersComponent) {

		List<ItemAttributeModifiers.Entry> sorted = new ObjectArrayList<>(modifiersComponent.modifiers());
		sorted.sort(Comparator.comparing(entry -> entry.modifier().operation().ordinal()));

		double baseValue = this.getAttributeBaseValue(context);
		double result = baseValue;

		for (var entry : sorted) {

			Holder<Attribute> attribute = entry.attribute();
			AttributeModifier modifier = entry.modifier();

			if (!Objects.equals(attribute, this.attribute())) {
				continue;
			}

			double amount = modifier.amount();
			result += switch (modifier.operation()) {
				case ADD_VALUE ->
					amount;
				case ADD_MULTIPLIED_BASE ->
					amount * baseValue;
				case ADD_MULTIPLIED_TOTAL ->
					amount * result;
			};

		}

		return result;

	}

	private double getAttributeBaseValue(Context context) {
		return entity()
			.flatMap(context::getOptional)
			.filter(LivingEntity.class::isInstance)
			.map(LivingEntity.class::cast)
			.filter(entity -> entity.getAttributes().hasAttribute(this.attribute()))
			.map(entity -> entity.getAttributeBaseValue(this.attribute()))
			.orElse(0.0d);
	}

}
