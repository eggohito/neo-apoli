package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.item.InvertedItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsEmptyItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public record HasEquippedItemEntityCondition(ItemCondition itemCondition, AttributeModifierSlot slot) implements EntityCondition {

	public static final MapCodec<HasEquippedItemEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemCondition.CODEC.optionalFieldOf("item_condition", new InvertedItemCondition(new IsEmptyItemCondition())).forGetter(HasEquippedItemEntityCondition::itemCondition),
		AttributeModifierSlot.CODEC.fieldOf("slot").forGetter(HasEquippedItemEntityCondition::slot)
	).apply(instance, HasEquippedItemEntityCondition::new));

	public static final PacketCodec<RegistryByteBuf, HasEquippedItemEntityCondition> PACKET_CODEC = PacketCodec.tuple(
		ItemCondition.PACKET_CODEC, HasEquippedItemEntityCondition::itemCondition,
		AttributeModifierSlot.PACKET_CODEC, HasEquippedItemEntityCondition::slot,
		HasEquippedItemEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.HAS_EQUIPPED_ITEM;
	}

	@Override
	public boolean test(Context context) {

		if (!(context.nullable(ContextParameters.THIS_ENTITY) instanceof LivingEntity thisLiving)) {
			return false;
		}

		for (var equipmentSlot : EquipmentSlot.values()) {

			Context itemContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.ITEM))
				.add(ContextParameters.ITEM_STACK, thisLiving.getEquippedStack(equipmentSlot)));

			if (itemCondition().test(itemContext.makeChild(".item_condition"))) {
				return true;
			}

		}

		return false;

	}

	@Override
	public void validate(ErrorReporter reporter) {
		EntityCondition.super.validate(reporter);
		itemCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.ITEM))
			.makeChild(".item_condition"));
	}

}
