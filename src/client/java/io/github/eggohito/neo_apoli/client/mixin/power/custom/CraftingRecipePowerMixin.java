package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.recipe.PowerStackedItemContents;
import io.github.eggohito.neo_apoli.recipe.book.NeoApoliRecipeBookCategories;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public abstract class CraftingRecipePowerMixin {

	@Mixin(Minecraft.class)
	public static class PowerRecipeDisplayCache implements PowerRecipeDisplayHolder {

		@Unique
		private final Int2ObjectOpenHashMap<PowerReference> neo_apoli$referencesByIndex = new Int2ObjectOpenHashMap<>();

		@Override
		public Int2ObjectMap<PowerReference> neo_apoli$getReferencesByIndex() {
			return new Int2ObjectOpenHashMap<>(neo_apoli$referencesByIndex);
		}

		@Override
		public void neo_apoli$setReferencesByIndex(Int2ObjectMap<PowerReference> referencesByIndex) {

			this.neo_apoli$referencesByIndex.clear();

			this.neo_apoli$referencesByIndex.putAll(referencesByIndex);
			this.neo_apoli$referencesByIndex.trim();

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

		@Inject(method = "getTooltipText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeButton;hasMultipleRecipes()Z"))
		private void appendPowerRecipeTooltip(ItemStack stack, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> components) {

			Minecraft client = Minecraft.getInstance();
			PowersComponent powersComponent = NeoApoliEntityComponents.POWERS.maybeGet(client.player).orElse(null);

			if (powersComponent == null) {
				return;
			}

			Int2ObjectMap<PowerReference> referencesByIndex = ((PowerRecipeDisplayHolder) client).neo_apoli$getReferencesByIndex();
			PowerReference reference = referencesByIndex.get(this.getCurrentRecipe().index());

			if (reference == null || !PowerManager.contains(reference) || powersComponent.hasInstance(reference)) {
				return;
			}

			components.add(Component.empty());
			components.add(Component.literal("Missing power: ").withStyle(ChatFormatting.RED).append(PowerManager.getEntry(reference).name()));

		}

	}

}
