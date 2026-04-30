package io.github.eggohito.neo_apoli.mixin.impl.power.custom.crafting_recipe;

import io.github.eggohito.neo_apoli.impl.misc.PowerCraftingInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TransientCraftingContainer.class)
public abstract class TransientCraftingContainerMixin implements PowerCraftingInventory {

	@Unique
	private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

	@Override
	public TransientCraftingContainer neo_apoli$getInventory() {
		return (TransientCraftingContainer) (Object) this;
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
