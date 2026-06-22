package io.github.eggohito.neo_apoli.mixin.impl.misc.power_crafting;

import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.impl.misc.PowerCrafting;
import io.github.eggohito.neo_apoli.power.Power;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.Objects;

@Mixin(TransientCraftingContainer.class)
public abstract class TransientCraftingContainerMixin implements PowerCrafting {

	@Unique
	private final ThreadLocal<Map<? extends Power.Instance<?>, Context>> neo_apoli$modifyingInstances = ThreadLocal.withInitial(Object2ObjectLinkedOpenHashMap::new);

	@Unique
	private final ThreadLocal<Entity> neo_apoli$entity = new ThreadLocal<>();

	@Override
	public Map<? extends Power.Instance<?>, Context> neo_apoli$getModifyingInstances() {
		return Objects.requireNonNullElseGet(neo_apoli$modifyingInstances.get(), Map::of);
	}

	@Override
	public void neo_apoli$setModifyingInstances(Map<? extends Power.Instance<?>, Context> modifyingInstances) {

		if (modifyingInstances == null || modifyingInstances.isEmpty()) {
			this.neo_apoli$modifyingInstances.remove();
		}

		else {
			this.neo_apoli$modifyingInstances.set(modifyingInstances);
		}

	}

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
