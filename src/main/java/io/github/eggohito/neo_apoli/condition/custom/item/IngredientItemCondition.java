package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.ItemCondition;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;

@EqualsAndHashCode
@Data
public final class IngredientItemCondition extends ItemCondition {

	public static final MapCodec<IngredientItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientItemCondition::ingredient)
	).apply(instance, IngredientItemCondition::new));

	public static final PacketCodec<RegistryByteBuf, IngredientItemCondition> PACKET_CODEC = Ingredient.PACKET_CODEC.xmap(
		IngredientItemCondition::new,
		IngredientItemCondition::ingredient
	);

	private final Ingredient ingredient;

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.INGREDIENT;
	}

	@Override
	protected boolean impl(Context context) {
		return this.ingredient().test(context.required(ContextParameters.ITEM_STACK));
	}

}
