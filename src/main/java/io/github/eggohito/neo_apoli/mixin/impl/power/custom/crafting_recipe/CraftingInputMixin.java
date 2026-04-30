package io.github.eggohito.neo_apoli.mixin.impl.power.custom.crafting_recipe;

import io.github.eggohito.neo_apoli.impl.misc.PowerCraftingInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CraftingInput.class)
public abstract class CraftingInputMixin implements PowerCraftingInventory {

	@Unique
	private final ThreadLocal<TransientCraftingContainer> neo_apoli$inventory = new ThreadLocal<>();

	@Unique
	private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

	@Override
	public TransientCraftingContainer neo_apoli$getInventory() {
		return this.neo_apoli$inventory.get();
	}

	@Override
	public void neo_apoli$setInventory(TransientCraftingContainer inventory) {

		if (inventory == null) {
			this.neo_apoli$inventory.remove();
		}

		else {
			this.neo_apoli$inventory.set(inventory);
		}

	}

	@Override
	public @Nullable Entity neo_apoli$getEntity() {
		return this.neo_apoli$entity.get();
	}

	@Override
	public void neo_apoli$setEntity(@Nullable Entity entity) {

		if (entity == null) {
			this.neo_apoli$entity.remove();
		}

		else {
			this.neo_apoli$entity.set(entity);
		}

	}

}
