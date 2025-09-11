package io.github.eggohito.neo_apoli.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliMapCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.duck.PowerCraftingInventory;
import io.github.eggohito.neo_apoli.power.custom.CraftingRecipePower;
import io.github.eggohito.neo_apoli.recipe.book.NeoApoliRecipeBookCategories;
import io.github.eggohito.neo_apoli.util.PowerReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public record PowerCraftingRecipe(PowerReference powerReference, CraftingRecipe delegate) implements CraftingRecipe {

	@Override
	public boolean matches(CraftingRecipeInput input, World world) {

		if (!(input instanceof PowerCraftingInventory powerCraftingInventory)) {
			return false;
		}

		CraftingRecipe powerDefinedRecipe = NeoApoliEntityComponents.POWERS.maybeGet(powerCraftingInventory.neo_apoli$getEntity())
			.filter(powersComponent -> powersComponent.hasInstance(this.powerReference()))
			.map(powersComponent -> powersComponent.getInstance(this.powerReference()))
			.filter(CraftingRecipePower.Instance.class::isInstance)
			.map(CraftingRecipePower.Instance.class::cast)
			.map(CraftingRecipePower.Instance::getRecipeEntry)
			.map(RecipeEntry::value)
			.orElse(null);

		return Objects.equals(delegate(), powerDefinedRecipe)
			&& delegate().matches(input, world);

	}

	@Override
	public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
		return delegate().craft(input, registries);
	}

	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return NeoApoliRecipeSerializers.POWER_CRAFTING;
	}

	@Override
	public IngredientPlacement getIngredientPlacement() {
		return delegate().getIngredientPlacement();
	}

	@Override
	public CraftingRecipeCategory getCategory() {
		return delegate().getCategory();
	}

	@Override
	public RecipeBookCategory getRecipeBookCategory() {
		return NeoApoliRecipeBookCategories.POWER_CRAFTING_RECIPE;
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return delegate().isIgnoredInRecipeBook();
	}

	@Override
	public boolean showNotification() {
		return delegate().showNotification();
	}

	@Override
	public List<RecipeDisplay> getDisplays() {
		return delegate().getDisplays();
	}

	public static class Serializer implements RecipeSerializer<PowerCraftingRecipe> {

		public static final MapCodec<PowerCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			PowerReference.CODEC.fieldOf("power").forGetter(PowerCraftingRecipe::powerReference),
			NeoApoliMapCodecs.CRAFTING_RECIPE.codec().fieldOf("recipe").forGetter(PowerCraftingRecipe::delegate)
		).apply(instance, PowerCraftingRecipe::new));

		public static final PacketCodec<RegistryByteBuf, PowerCraftingRecipe> PACKET_CODEC = PacketCodec.tuple(
			PowerReference.PACKET_CODEC, PowerCraftingRecipe::powerReference,
			NeoApoliPacketCodecs.CRAFTING_RECIPE, PowerCraftingRecipe::delegate,
			PowerCraftingRecipe::new
		);

		@Override
		public MapCodec<PowerCraftingRecipe> codec() {
			return CODEC;
		}

		@Override
		public PacketCodec<RegistryByteBuf, PowerCraftingRecipe> packetCodec() {
			return PACKET_CODEC;
		}

	}

}
