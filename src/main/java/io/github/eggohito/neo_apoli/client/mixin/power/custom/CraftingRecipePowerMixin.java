package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.duck.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.recipe.PowerRecipeFinder;
import io.github.eggohito.neo_apoli.recipe.book.NeoApoliRecipeBookCategories;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CraftingRecipePowerMixin {

	@Mixin(MinecraftClient.class)
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

	@Mixin(RecipeResultCollection.class)
	public static abstract class RecipeResultCollectionFilter {

		@ModifyExpressionValue(method = "populateRecipes", at = @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
		private boolean test(boolean original, @Local RecipeDisplayEntry currentDisplayEntry) {

			if (original) {

				MinecraftClient client = MinecraftClient.getInstance();
				Map<RecipeDisplayEntry, PowerReference> referencesByDisplayEntry = ((PowerRecipeDisplayHolder) client).neo_apoli$getReferencesByDisplayEntry();

				for (Map.Entry<RecipeDisplayEntry, PowerReference> mapEntry : referencesByDisplayEntry.entrySet()) {

					RecipeDisplayEntry displayEntry = mapEntry.getKey();
					PowerReference powerReference = mapEntry.getValue();

					if (Objects.equals(displayEntry.id(), currentDisplayEntry.id()) && PowerManager.contains(powerReference)) {
						return shouldShowWhenUngranted(powerReference)
							|| hasPower(powerReference, client.player);
					}

				}

				return true;

			}

			else {
				return false;
			}

		}

		@Unique
		private static boolean shouldShowWhenUngranted(PowerReference reference) {
			return PowerManager.getAsResult(reference)
				.result()
				.filter(CraftingRecipePower.class::isInstance)
				.map(CraftingRecipePower.class::cast)
				.map(CraftingRecipePower::shouldShowWhenUngranted)
				.orElse(false);
		}

		@Unique
		private static boolean hasPower(PowerReference reference, Entity entity) {
			return NeoApoliEntityComponents.POWERS.maybeGet(entity)
				.stream()
				.filter(powersComponent -> powersComponent.hasInstance(reference))
				.map(powersComponent -> powersComponent.getInstance(reference))
				.anyMatch(CraftingRecipePower.Instance.class::isInstance);
		}

	}

	@Mixin(AbstractCraftingRecipeBookWidget.class)
	public static abstract class CraftingRecipeBookWidgetProxy extends RecipeBookWidget<AbstractCraftingScreenHandler> {

		@Mutable
		@Shadow
		@Final
		private static List<Tab> TABS;

		private CraftingRecipeBookWidgetProxy(AbstractCraftingScreenHandler craftingScreenHandler, List<Tab> tabs) {
			super(craftingScreenHandler, tabs);
		}

		@ModifyArg(method = "populateRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/recipebook/RecipeResultCollection;populateRecipes(Lnet/minecraft/recipe/RecipeFinder;Ljava/util/function/Predicate;)V"))
		private RecipeFinder overrideRecipeFinder(RecipeFinder original) {
			return new PowerRecipeFinder(this.client.player, original, ((PowerRecipeDisplayHolder) this.client));
		}

		//	Adds a new tab to the recipe book used for storing recipes added by powers
		static {
			TABS = ImmutableList.<Tab>builder()
				.addAll(TABS)
				.add(new Tab(Items.COMMAND_BLOCK, NeoApoliRecipeBookCategories.POWER_CRAFTING_RECIPE))
				.build();
		}

	}

	@Mixin(AnimatedResultButton.class)
	public static abstract class CustomPowerRecipeTooltip {

		@Shadow
		public abstract NetworkRecipeId getCurrentId();

		@ModifyReturnValue(method = "getTooltip", at = @At("RETURN"))
		private List<Text> appendPowerRecipeTooltip(List<Text> original) {

			Map<RecipeDisplayEntry, PowerReference> referencesByDisplayEntry = ((PowerRecipeDisplayHolder) MinecraftClient.getInstance()).neo_apoli$getReferencesByDisplayEntry();

			PowerReference powerReference = null;
			Power power = null;

			for (Map.Entry<RecipeDisplayEntry, PowerReference> mapEntry : referencesByDisplayEntry.entrySet()) {

				RecipeDisplayEntry displayEntry = mapEntry.getKey();
				powerReference = mapEntry.getValue();

				if (Objects.equals(displayEntry.id(), this.getCurrentId()) && PowerManager.contains(powerReference)) {
					power = PowerManager.get(powerReference);
					break;
				}

			}

			if (powerReference != null && power != null) {

				PowerReference finalPowerReference = powerReference;
				boolean hasPower = NeoApoliEntityComponents.POWERS.maybeGet(MinecraftClient.getInstance().player)
					.stream()
					.anyMatch(powersComponent -> powersComponent.hasInstance(finalPowerReference));

				Formatting formatting = hasPower
					? Formatting.GREEN
					: Formatting.RED;

				original.add(Text.empty());
				original.add(Text.literal("").append(Text.literal("Requires power: ").formatted(formatting)).append(power.getName()));

			}

			return original;

		}

	}

}
