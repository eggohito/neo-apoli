package io.github.eggohito.neo_apoli.container_type;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;

public record PresetContainerType(int columns, int rows, PresetFactory presetFactory) implements ContainerType {

	@Override
	public MenuConstructor create(Container inventory) {
		return presetFactory().create(inventory, this.columns(), this.rows());
	}

	@FunctionalInterface
	public interface PresetFactory {
		MenuConstructor create(Container inventory, int columns, int rows);
	}

}
