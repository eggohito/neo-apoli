package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.PowerManager;
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
		private final Int2ObjectOpenHashMap<PowerIdentifier> neo_apoli$powerIdsByIndex = new Int2ObjectOpenHashMap<>();

		@Override
		public Int2ObjectMap<PowerIdentifier> neo_apoli$getPowerIdsByIndex() {
			return new Int2ObjectOpenHashMap<>(neo_apoli$powerIdsByIndex);
		}

		@Override
		public void neo_apoli$setPowerIdsByIndex(Int2ObjectMap<PowerIdentifier> powerIdsByIndex) {

			this.neo_apoli$powerIdsByIndex.clear();

			this.neo_apoli$powerIdsByIndex.putAll(powerIdsByIndex);
			this.neo_apoli$powerIdsByIndex.trim();

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
			Powers powers = Powers.getNullable(client.player);

			if (powers == null) {
				return;
			}

			Int2ObjectMap<PowerIdentifier> powerIdsByIndex = ((PowerRecipeDisplayHolder) client).neo_apoli$getPowerIdsByIndex();
			PowerIdentifier powerId = powerIdsByIndex.get(this.getCurrentRecipe().index());

			if (powerId == null || !PowerManager.contains(powerId) || powers.hasInstance(powerId)) {
				return;
			}

			components.add(Component.empty());
			components.add(Component.literal("Missing power: ").withStyle(ChatFormatting.RED).append(PowerManager.getHolder(powerId).name()));

		}

	}

}
