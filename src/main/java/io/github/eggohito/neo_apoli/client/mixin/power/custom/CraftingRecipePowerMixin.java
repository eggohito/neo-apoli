package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.recipe.PowerStackedItemContents;
import io.github.eggohito.neo_apoli.recipe.book.NeoApoliRecipeBookCategories;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CraftingRecipePowerMixin {

	@Mixin(Minecraft.class)
	public static class PowerRecipeDisplayCache implements PowerRecipeDisplayHolder {

		@Unique
		private final Object2ObjectOpenHashMap<RecipeDisplayEntry, PowerReference> neo_apoli$referencesByDisplayEntry = new Object2ObjectOpenHashMap<>();

		@Override
		public Map<RecipeDisplayEntry, PowerReference> neo_apoli$getReferencesByDisplayEntry() {
			return new Object2ObjectOpenHashMap<>(neo_apoli$referencesByDisplayEntry);
		}

		@Override
		public void neo_apoli$setReferencesByDisplayEntry(Map<RecipeDisplayEntry, PowerReference> referencesByDisplay) {

			this.neo_apoli$referencesByDisplayEntry.clear();

			this.neo_apoli$referencesByDisplayEntry.putAll(referencesByDisplay);
			this.neo_apoli$referencesByDisplayEntry.trim();

		}

	}

	@Mixin(RecipeCollection.class)
	public static abstract class RecipeCollectionFilter {

		@ModifyExpressionValue(method = "selectRecipes", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
		private boolean test(boolean original, @Local RecipeDisplayEntry currentDisplayEntry) {

			if (original) {

				Minecraft client = Minecraft.getInstance();
				Map<RecipeDisplayEntry, PowerReference> referencesByDisplayEntry = ((PowerRecipeDisplayHolder) client).neo_apoli$getReferencesByDisplayEntry();

				for (Map.Entry<RecipeDisplayEntry, PowerReference> mapEntry : referencesByDisplayEntry.entrySet()) {

					RecipeDisplayEntry displayEntry = mapEntry.getKey();
					PowerReference powerReference = mapEntry.getValue();

					if (Objects.equals(displayEntry.id(), currentDisplayEntry.id()) && PowerManager.contains(powerReference)) {
						return true;
					}

				}

				return true;

			}

			else {
				return false;
			}

		}

	}

	@Mixin(CraftingRecipeBookComponent.class)
	public static abstract class CraftingRecipeBookComponentProxy extends RecipeBookComponent<AbstractCraftingMenu> {

		@Mutable
		@Shadow
		@Final
		private static List<TabInfo> TABS;

		private CraftingRecipeBookComponentProxy(AbstractCraftingMenu craftingScreenHandler, List<TabInfo> tabs) {
			super(craftingScreenHandler, tabs);
		}

		@ModifyArg(method = "selectMatchingRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeCollection;selectRecipes(Lnet/minecraft/world/entity/player/StackedItemContents;Ljava/util/function/Predicate;)V"))
		private StackedItemContents overrideRecipeFinder(StackedItemContents original) {
			return new PowerStackedItemContents(this.minecraft.player, original, ((PowerRecipeDisplayHolder) this.minecraft));
		}

		//	Adds a new tab to the recipe book used for storing recipes added by powers
		static {
			TABS = ImmutableList.<TabInfo>builder()
				.addAll(TABS)
				.add(new TabInfo(Items.COMMAND_BLOCK, NeoApoliRecipeBookCategories.POWER_CRAFTING_RECIPE))
				.build();
		}

	}

	@Mixin(RecipeButton.class)
	public static abstract class CustomPowerRecipeTooltip {

		@Shadow
		public abstract RecipeDisplayId getCurrentRecipe();

		@ModifyReturnValue(method = "getTooltipText", at = @At("RETURN"))
		private List<Component> appendPowerRecipeTooltip(List<Component> original) {

			Map<RecipeDisplayEntry, PowerReference> referencesByDisplayEntry = ((PowerRecipeDisplayHolder) Minecraft.getInstance()).neo_apoli$getReferencesByDisplayEntry();
			PowerEntry<?> entry = null;

			for (Map.Entry<RecipeDisplayEntry, PowerReference> mapEntry : referencesByDisplayEntry.entrySet()) {

				RecipeDisplayEntry displayEntry = mapEntry.getKey();
				PowerReference powerReference = mapEntry.getValue();

				if (Objects.equals(displayEntry.id(), this.getCurrentRecipe()) && PowerManager.contains(powerReference)) {
					entry = PowerManager.getEntry(powerReference);
					break;
				}

			}

			if (entry != null) {

				PowerEntry<?> finalEntry = entry;
				boolean hasPower = NeoApoliEntityComponents.POWERS.maybeGet(Minecraft.getInstance().player)
					.stream()
					.anyMatch(powersComponent -> powersComponent.hasInstance(finalEntry.reference()));

				ChatFormatting formatting = hasPower
					? ChatFormatting.GREEN
					: ChatFormatting.RED;

				original.add(Component.empty());
				original.add(Component.literal("").append(Component.literal("Requires power: ").withStyle(formatting)).append(finalEntry.name()));

			}

			return original;

		}

	}

}
