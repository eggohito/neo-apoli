package io.github.eggohito.neo_apoli.container_menu.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.container_menu.ContainerMenu;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;

import java.util.function.Function;

@Accessors(fluent = true)
public final class SimpleContainerMenu implements ContainerMenu.Type<SimpleContainerMenu>, ContainerMenu {

	public static final Codec<SimpleContainerMenu> CODEC = Type.CODEC.comapFlatMap(SimpleContainerMenu::validate, Function.identity());

	private final Factory factory;

	@Getter
	private final MapCodec<SimpleContainerMenu> mapCodec = MapCodec.unit(this);
	@Getter
	private final StreamCodec<RegistryFriendlyByteBuf, SimpleContainerMenu> streamCodec = StreamCodec.unit(this);

	@Getter
	private final int columns;
	@Getter
	private final int rows;

	public SimpleContainerMenu(int columns, int rows, Factory factory) {
		this.factory = factory;
		this.columns = columns;
		this.rows = rows;
	}

	@Override
	public SimpleContainerMenu getType() {
		return this;
	}

	@Override
	public MenuConstructor constructor(Container container) {
		return (id, inventory, player) -> factory.create(id, inventory, player, container, columns(), rows());
	}

	private static DataResult<SimpleContainerMenu> validate(Type<?> type) {

		if (type instanceof SimpleContainerMenu self) {
			return DataResult.success(self);
		}

		else {
			return DataResult.error(() -> "Container menu type \"" + RegistryUtil.getId(NeoApoliRegistries.CONTAINER_MENU_TYPE, type) + "\" requires parameters!");
		}

	}

	@FunctionalInterface
	public interface Factory {
		AbstractContainerMenu create(int id, Inventory inventory, Player player, Container container, int columns, int rows);
	}

}
