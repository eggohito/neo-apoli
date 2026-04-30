package io.github.eggohito.neo_apoli.mixin.impl.power.custom.crafting_recipe;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.recipe.PowerStackedItemContents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(RecipeDisplayEntry.class)
public abstract class RecipeDisplayEntryMixin {

	@Shadow
	public abstract RecipeDisplayId id();

	@WrapOperation(method = "canCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/StackedItemContents;canCraft(Ljava/util/List;Lnet/minecraft/world/entity/player/StackedContents$Output;)Z"))
	boolean accountForPowerCraftingRecipeDisplays(StackedItemContents contents, List<? extends StackedContents.IngredientInfo<Holder<Item>>> rawIngredients, StackedContents.@Nullable Output<Holder<Item>> itemCallback, Operation<Boolean> original) {

		if (contents instanceof PowerStackedItemContents powerContents) {
			return powerContents.isCraftable(this.id(), rawIngredients, 1, itemCallback);
		}

		else {
			return original.call(contents, rawIngredients, itemCallback);
		}

	}

}
