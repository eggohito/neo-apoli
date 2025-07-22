package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsEmptyItemCondition;
import io.github.eggohito.neo_apoli.condition.meta.item.InvertedItemCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

@EqualsAndHashCode
@Data
public final class EquippedItemEntityCondition extends EntityCondition {

	public static final MapCodec<EquippedItemEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemCondition.CODEC.optionalFieldOf("item_condition", new InvertedItemCondition(new IsEmptyItemCondition())).forGetter(EquippedItemEntityCondition::itemCondition),
		AttributeModifierSlot.CODEC.fieldOf("slot").forGetter(EquippedItemEntityCondition::slot)
	).apply(instance, EquippedItemEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, EquippedItemEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		ItemCondition.PACKET_CODEC, EquippedItemEntityCondition::itemCondition,
		AttributeModifierSlot.PACKET_CODEC, EquippedItemEntityCondition::slot,
		EquippedItemEntityCondition::new
	);

	private final ItemCondition itemCondition;
	private final AttributeModifierSlot slot;

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.EQUIPPED_ITEM;
	}

	@Override
	protected boolean impl(Context context) {

		if (!(context.required(ContextParameters.ENTITY) instanceof LivingEntity livingEntity)) {
			return false;
		}

		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {

			if (slot().matches(equipmentSlot)) {

				Context itemContext = context.copy(builder -> builder
					.withContextType(ContextTypes.merge(context.getType(), ContextTypes.ITEM))
					.add(ContextParameters.ITEM_STACK, livingEntity.getEquippedStack(equipmentSlot)));

				if (itemCondition().test(itemContext.makeChild(".item_condition"))) {
					return true;
				}

			}

		}

		return false;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		super.validate(reporter);
		itemCondition().validate(reporter
			.withContextType(ContextTypes.merge(reporter.getContextType(), ContextTypes.ITEM))
			.makeChild(".item_condition"));
	}

}
