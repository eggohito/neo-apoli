package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.container_menu.ContainerMenu;
import io.github.eggohito.neo_apoli.container_menu.custom.DynamicContainerMenu;
import io.github.eggohito.neo_apoli.container_menu.custom.SimpleContainerMenu;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.inventory.MenuType;

@SuppressWarnings("unused")
public final class NeoApoliContainerMenuTypes {

	//  Simple container menus
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_9X1 = registerSimpleInternal("generic_9x1", 9, 1, (id, inventory, player, container, columns, rows) -> new ChestMenu(MenuType.GENERIC_9x1, id, inventory, container, rows));
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_9X2 = registerSimpleInternal("generic_9x2", 9, 2, (id, inventory, player, container, columns, rows) -> new ChestMenu(MenuType.GENERIC_9x2, id, inventory, container, rows));
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_9X3 = registerSimpleInternal("generic_9x3", 9, 3, (id, inventory, player, container, columns, rows) -> new ChestMenu(MenuType.GENERIC_9x3, id, inventory, container, rows));
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_9X4 = registerSimpleInternal("generic_9x4", 9, 4, (id, inventory, player, container, columns, rows) -> new ChestMenu(MenuType.GENERIC_9x4, id, inventory, container, rows));
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_9X5 = registerSimpleInternal("generic_9x5", 9, 5, (id, inventory, player, container, columns, rows) -> new ChestMenu(MenuType.GENERIC_9x5, id, inventory, container, rows));
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_9X6 = registerSimpleInternal("generic_9x6", 9, 6, (id, inventory, player, container, columns, rows) -> new ChestMenu(MenuType.GENERIC_9x6, id, inventory, container, rows));
	public static final ContainerMenu.Type<SimpleContainerMenu> GENERIC_3X3 = registerSimpleInternal("generic_3x3", 3, 3, (id, inventory, player, container, columns, rows) -> new DispenserMenu(id, inventory, container));
	public static final ContainerMenu.Type<SimpleContainerMenu> HOPPER = registerSimpleInternal("hopper", 5, 1, (id, inventory, player, container, columns, rows) -> new HopperMenu(id, inventory, container));

	//  Complex container menus
	public static final ContainerMenu.Type<DynamicContainerMenu> DYNAMIC = registerInternal("dynamic", DynamicContainerMenu.CODEC, DynamicContainerMenu.STREAM_CODEC);

	//  TODO: Create custom screen handlers for more customization
	public static void registerAll() {

		ContainerMenu.Type.ALIASES.addPathAlias("chest", GENERIC_9X3);
		ContainerMenu.Type.ALIASES.addPathAlias("double_chest", GENERIC_9X6);

		ContainerMenu.Type.ALIASES.addPathAlias("dropper", GENERIC_3X3);
		ContainerMenu.Type.ALIASES.addPathAlias("dispenser", GENERIC_3X3);

	}

	public static <C extends ContainerMenu> ContainerMenu.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.CONTAINER_MENU_TYPE, id, new ContainerMenu.Type<>() {

			@Override
			public MapCodec<C> mapCodec() {
				return mapCodec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, C> streamCodec() {
				return streamCodec;
			}

		});
	}

	private static <C extends ContainerMenu> ContainerMenu.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static ContainerMenu.Type<SimpleContainerMenu> registerSimple(ResourceLocation id, int columns, int rows, SimpleContainerMenu.Factory factory) {
		return Registry.register(NeoApoliRegistries.CONTAINER_MENU_TYPE, id, new SimpleContainerMenu(columns, rows, factory));
	}

	private static ContainerMenu.Type<SimpleContainerMenu> registerSimpleInternal(String path, int columns, int rows, SimpleContainerMenu.Factory factory) {
		return registerSimple(NeoApoli.id(path), columns, rows, factory);
	}

}
