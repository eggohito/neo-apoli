package io.github.eggohito.neo_apoli.recipe;

import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Getter
public class PowerStackedItemContents extends StackedItemContents {

	private final Player entity;
	private final PowerRecipeDisplayHolder displayHolder;

	public PowerStackedItemContents(Player entity, StackedItemContents delegate, PowerRecipeDisplayHolder displayHolder) {
		this.entity = entity;
		this.raw = delegate.raw;
		this.displayHolder = displayHolder;
	}

	public boolean isCraftable(RecipeDisplayId id, List<? extends StackedContents.IngredientInfo<Holder<Item>>> rawIngredients, int quantity, @Nullable StackedContents.Output<Holder<Item>> itemCallback) {

		if (this.raw.tryPick(rawIngredients, quantity, itemCallback)) {

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
