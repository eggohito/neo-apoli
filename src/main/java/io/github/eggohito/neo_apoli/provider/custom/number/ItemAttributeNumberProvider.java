package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.provider.custom.item.ItemProvider;
import io.github.eggohito.neo_apoli.registry.provider.NeoApoliNumberProviderTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ItemAttributeNumberProvider(Holder<Attribute> attribute, ItemProvider item, Optional<EntityProvider> entity) implements NumberProvider {

	public static final MapCodec<ItemAttributeNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Attribute.CODEC.fieldOf("attribute").forGetter(ItemAttributeNumberProvider::attribute),
		ItemProvider.CODEC.fieldOf("item").forGetter(ItemAttributeNumberProvider::item),
		EntityProvider.CODEC.optionalFieldOf("entity").forGetter(ItemAttributeNumberProvider::entity)
	).apply(instance, ItemAttributeNumberProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemAttributeNumberProvider> STREAM_CODEC = StreamCodec.composite(
		Attribute.STREAM_CODEC, ItemAttributeNumberProvider::attribute,
		ItemProvider.STREAM_CODEC, ItemAttributeNumberProvider::item,
		ByteBufCodecs.optional(EntityProvider.STREAM_CODEC), ItemAttributeNumberProvider::entity,
		ItemAttributeNumberProvider::new
	);

	@Override
	public @NotNull NumberProvider.Type<?> getType() {
		return NeoApoliNumberProviderTypes.ITEM_ATTRIBUTE;
	}

	@Override
	public double getDouble(Context context) {

		Context itemContext = context.forChild(".item");
		ItemStack item = item().getItem(itemContext);

		if (itemContext.hasProblems()) {
			return 0.0D;
		}

		else {
			return this.compute(context, item.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY));
		}

	}

	@Override
	public void validate(Context.Validator validator) {
		NumberProvider.super.validate(validator);
		item().validate(validator.forChild(".item"));
		entity().ifPresent(entity -> entity.validate(validator.forChild(".entity")));
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
			.flatMap(entity -> entity.getEntity(context.forChild(".entity")))
			.filter(LivingEntity.class::isInstance)
			.map(LivingEntity.class::cast)
			.filter(entity -> entity.getAttributes().hasAttribute(this.attribute()))
			.map(entity -> entity.getAttributeBaseValue(this.attribute()))
			.orElse(0.0D);
	}

}
