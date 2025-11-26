package io.github.eggohito.neo_apoli.util.container_type;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.MenuType;

public final class NeoApoliContainerTypes {

	//	Presets for generic container screen handlers
	public static final PresetContainerType GENERIC_9X1 = registerInternal("generic_9x1", new PresetContainerType(9, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x1, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X2 = registerInternal("generic_9x2", new PresetContainerType(9, 2, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x2, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X3 = registerInternal("generic_9x3", new PresetContainerType(9, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x3, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X4 = registerInternal("generic_9x4", new PresetContainerType(9, 4, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x4, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X5 = registerInternal("generic_9x5", new PresetContainerType(9, 5, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x5, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X6 = registerInternal("generic_9x6", new PresetContainerType(9, 6, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new ChestMenu(MenuType.GENERIC_9x6, syncId, playerInventory, inventory, rows)));

	//	Presets for other container screen handlers
	public static final PresetContainerType GENERIC_3X3 = registerInternal("generic_3x3", new PresetContainerType(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new DispenserMenu(syncId, playerInventory, inventory)));
	public static final PresetContainerType HOPPER = registerInternal("hopper", new PresetContainerType(5, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new HopperMenu(syncId, playerInventory)));

	public static void registerAll() {

		//	TODO: Create custom screen handlers for chest/double chest/dropper/dispenser for more customization
		ContainerType.ALIASES.addPathAlias("chest", GENERIC_9X3);
		ContainerType.ALIASES.addPathAlias("double_chest", GENERIC_9X6);

		ContainerType.ALIASES.addPathAlias("dropper", GENERIC_3X3);
		ContainerType.ALIASES.addPathAlias("dispenser", GENERIC_3X3);

	}

	private static <C extends ContainerType> C registerInternal(String path, C containerType) {
		return register(NeoApoli.id(path), containerType);
	}

	public static <C extends ContainerType> C register(ResourceLocation id, C containerType) {
		return Registry.register(NeoApoliRegistries.CONTAINER_TYPE, id, containerType);
	}

}
