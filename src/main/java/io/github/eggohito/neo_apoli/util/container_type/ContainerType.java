package io.github.eggohito.neo_apoli.util.container_type;

import io.github.eggohito.neo_apoli.util.TextAlignment;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandlerFactory;
import org.jetbrains.annotations.Range;

public interface ContainerType {

	default TextAlignment textAlignment() {
		return TextAlignment.NONE;
	}

	ScreenHandlerFactory create(Inventory inventory);

	@Range(from = 1, to = Integer.MAX_VALUE)
	int columns();

	@Range(from = 1, to = Integer.MAX_VALUE)
	int rows();

}
