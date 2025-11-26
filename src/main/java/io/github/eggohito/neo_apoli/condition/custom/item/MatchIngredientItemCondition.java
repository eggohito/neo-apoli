package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionType;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record MatchIngredientItemCondition(Ingredient ingredient) implements ItemCondition {

	public static final MapCodec<MatchIngredientItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(MatchIngredientItemCondition::ingredient))
		.apply(instance, MatchIngredientItemCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, MatchIngredientItemCondition> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, MatchIngredientItemCondition::ingredient,
		MatchIngredientItemCondition::new
	);

	@Override
	public ItemConditionType<?> getType() {
		return ItemConditionTypes.MATCH_INGREDIENT;
	}

	@Override
	public boolean test(Context context) {
		return context.optional(NeoApoliContextKeys.ITEM_STACK)
			.stream()
			.anyMatch(ingredient());
	}

}
