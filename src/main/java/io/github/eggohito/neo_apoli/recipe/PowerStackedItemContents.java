package io.github.eggohito.neo_apoli.recipe;

import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

			for (var entry : this.getDisplayHolder().neo_apoli$getReferencesByIndex().int2ObjectEntrySet()) {

				int index = entry.getIntKey();
				PowerReference reference = entry.getValue();

				if (id.index() == index) {
					return Powers.getOptional(this.getEntity())
						.flatMap(powers -> powers.getOptionalInstance(reference))
						.stream()
						.anyMatch(CraftingRecipePower.Instance.class::isInstance);
				}

			}

			return false;

		}

		else {
			return false;
		}

	}

}
