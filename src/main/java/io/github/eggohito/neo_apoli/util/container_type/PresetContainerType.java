package io.github.eggohito.neo_apoli.util.container_type;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerFactory;

public record PresetContainerType(int columns, int rows, PresetFactory presetFactory) implements ContainerType {

	@Override
	public ScreenHandlerFactory create(Inventory inventory) {
		return presetFactory().create(inventory, this.columns(), this.rows());
	}

	@FunctionalInterface
	public interface PresetFactory {
		ScreenHandlerFactory create(Inventory inventory, int columns, int rows);
	}

}
