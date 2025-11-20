package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;

public record MatchIngredientItemCondition(Ingredient ingredient) implements ItemCondition {

	public static final MapCodec<MatchIngredientItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(MatchIngredientItemCondition::ingredient))
		.apply(instance, MatchIngredientItemCondition::new));

	public static final PacketCodec<RegistryByteBuf, MatchIngredientItemCondition> PACKET_CODEC = PacketCodec.tuple(
		Ingredient.PACKET_CODEC, MatchIngredientItemCondition::ingredient,
		MatchIngredientItemCondition::new
	);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.MATCH_INGREDIENT;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextParameters.ITEM_STACK)
			.stream()
			.anyMatch(ingredient());
	}

}
