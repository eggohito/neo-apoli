package io.github.eggohito.neo_apoli.condition.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.custom.item.InvertedItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.IsEmptyItemCondition;
import io.github.eggohito.neo_apoli.condition.custom.item.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionType;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;

public record HasEquippedItemEntityCondition(ItemCondition itemCondition, EquipmentSlotGroup slot) implements EntityCondition {

	private static final ContextKeySet CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.ITEM_STACK)
		.build();

	public static final MapCodec<HasEquippedItemEntityCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemCondition.CODEC.optionalFieldOf("item_condition", new InvertedItemCondition(IsEmptyItemCondition.INSTANCE)).forGetter(HasEquippedItemEntityCondition::itemCondition),
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

		if (!(context.getNullable(NeoApoliContextParams.THIS_ENTITY) instanceof LivingEntity thisLiving)) {
			return false;
		}

		for (var equipmentSlot : EquipmentSlot.values()) {

			if (!slot().test(equipmentSlot)) {
				continue;
			}

			Context conditionContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.ITEM_STACK, thisLiving.getItemBySlot(equipmentSlot))
				.build(context.level());

			if (itemCondition().test(conditionContext.forChild(".item_condition"))) {
				return true;
			}

		}

		return false;

	}

	@Override
	public void validate(Context.Validator validator) {
		EntityCondition.super.validate(validator);
		itemCondition().validate(validator.withAdditionalKeysFromSets(CONDITION_CONTEXT).forChild(".item_condition"));
	}

}
