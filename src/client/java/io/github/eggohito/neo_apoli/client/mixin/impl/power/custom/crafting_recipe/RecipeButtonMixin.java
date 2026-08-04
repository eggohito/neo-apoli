package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.crafting_recipe;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.impl.misc.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeButton.class)
public abstract class RecipeButtonMixin {

	@Shadow
	public abstract RecipeDisplayId getCurrentRecipe();

	@Inject(method = "getTooltipText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/recipebook/RecipeButton;hasMultipleRecipes()Z"))
	void appendPowerRecipeTooltip(ItemStack stack, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> components) {

		Minecraft client = Minecraft.getInstance();
		Powers powers = Powers.getNullable(client.player);

		if (powers == null) {
			return;
		}

		Int2ObjectMap<PowerIdentifier> powerIdsByIndex = ((PowerRecipeDisplayHolder) client).neo_apoli$getPowerIdsByIndex();
		PowerIdentifier powerId = powerIdsByIndex.get(this.getCurrentRecipe().index());

		if (powerId == null || !PowerManager.getInstance().contains(powerId) || powers.hasInstance(powerId)) {
			return;
		}

		components.add(Component.empty());
		components.add(Component.translatable("power.type.neo-apoli.crafting_recipe.missing_power", PowerManager.getInstance().get(powerId).name().copy().withStyle(ChatFormatting.RED)));

	}

}
