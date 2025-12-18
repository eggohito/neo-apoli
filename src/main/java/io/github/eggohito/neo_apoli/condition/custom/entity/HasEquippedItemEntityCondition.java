package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.item.InvertedItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsEmptyItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextKeySetHelper;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeySets;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;

public record HasEquippedItemEntityCondition(ItemCondition itemCondition, EquipmentSlotGroup slot) implements EntityCondition {

	public static final MapCodec<HasEquippedItemEntityCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemCondition.CODEC.optionalFieldOf("item_condition", new InvertedItemCondition(new IsEmptyItemCondition())).forGetter(HasEquippedItemEntityCondition::itemCondition),
		EquipmentSlotGroup.CODEC.fieldOf("slot").forGetter(HasEquippedItemEntityCondition::slot)
	).apply(instance, HasEquippedItemEntityCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HasEquippedItemEntityCondition> STREAM_CODEC = StreamCodec.composite(
		ItemCondition.STREAM_CODEC, HasEquippedItemEntityCondition::itemCondition,
		EquipmentSlotGroup.STREAM_CODEC, HasEquippedItemEntityCondition::slot,
		HasEquippedItemEntityCondition::new
	);

	@Override
	public EntityConditionType<?> getType() {
		return EntityConditionTypes.HAS_EQUIPPED_ITEM;
	}

	@Override
	public boolean test(Context context) {

		if (!(context.nullable(NeoApoliContextKeys.THIS_ENTITY) instanceof LivingEntity thisLiving)) {
			return false;
		}

		for (var equipmentSlot : EquipmentSlot.values()) {

			if (!slot().test(equipmentSlot)) {
				continue;
			}

			Context itemContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.ITEM))
				.add(NeoApoliContextKeys.ITEM_STACK, thisLiving.getItemBySlot(equipmentSlot))
				.build(context.getLevel());

			if (itemCondition().test(itemContext.forChild(".item_condition"))) {
				return true;
			}

		}

		return false;

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		itemCondition().validate(validator
			.withKeySet(ContextKeySetHelper.merge(validator.getKeySet(), NeoApoliContextKeySets.ITEM))
			.forChild(".item_condition"));
	}

}
