package io.github.eggohito.neo_apoli.condition.custom.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliItemConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

public record MatchIngredientItemCondition(Ingredient ingredient) implements ItemCondition {

	public static final MapCodec<MatchIngredientItemCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(Ingredient.CODEC.fieldOf("ingredient").forGetter(MatchIngredientItemCondition::ingredient))
		.apply(instance, MatchIngredientItemCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, MatchIngredientItemCondition> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, MatchIngredientItemCondition::ingredient,
		MatchIngredientItemCondition::new
	);

	@Override
	public ItemCondition.Type<?> getType() {
		return NeoApoliItemConditionTypes.MATCH_INGREDIENT;
	}

	@Override
	public boolean test(Context context) {
		return context.getOptional(NeoApoliContextParams.ITEM_STACK)
			.stream()
			.anyMatch(ingredient());
	}

}
