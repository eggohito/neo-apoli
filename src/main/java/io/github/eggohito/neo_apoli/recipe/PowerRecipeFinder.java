package io.github.eggohito.neo_apoli.recipe;

import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.util.PowerReference;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Getter
public class PowerRecipeFinder extends RecipeFinder {

	private final PlayerEntity entity;
	private final PowerRecipeDisplayHolder displayHolder;

	public PowerRecipeFinder(PlayerEntity entity, RecipeFinder delegate, PowerRecipeDisplayHolder displayHolder) {
		this.entity = entity;
		this.recipeMatcher = delegate.recipeMatcher;
		this.displayHolder = displayHolder;
	}

	public boolean isCraftable(NetworkRecipeId id, List<? extends RecipeMatcher.RawIngredient<RegistryEntry<Item>>> rawIngredients, int quantity, @Nullable RecipeMatcher.ItemCallback<RegistryEntry<Item>> itemCallback) {

		if (this.recipeMatcher.match(rawIngredients, quantity, itemCallback)) {

			for (Map.Entry<RecipeDisplayEntry, PowerReference> mapEntry : this.getDisplayHolder().neo_apoli$getReferencesByDisplayEntry().entrySet()) {

				RecipeDisplayEntry displayEntry = mapEntry.getKey();
				PowerReference powerReference = mapEntry.getValue();

				if (displayEntry.id().equals(id)) {
					return NeoApoliEntityComponents.POWERS.maybeGet(this.getEntity())
						.filter(powersComponent -> powersComponent.hasInstance(powerReference))
						.map(powersComponent -> powersComponent.getInstance(powerReference))
						.map(CraftingRecipePower.Instance.class::isInstance)
						.orElse(false);
				}

			}

			return true;

		}

		else {
			return false;
		}

	}

}
