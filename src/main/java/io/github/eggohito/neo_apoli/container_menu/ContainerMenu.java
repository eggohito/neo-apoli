package io.github.eggohito.neo_apoli.container_menu;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.MultiAlternativeCodec;
import io.github.eggohito.neo_apoli.container_menu.custom.SimpleContainerMenu;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;

public interface ContainerMenu {

	Codec<ContainerMenu> CODEC = Codec.lazyInitialized(() -> new MultiAlternativeCodec<>(Type.CODEC.dispatch(ContainerMenu::getType, Type::mapCodec), SimpleContainerMenu.CODEC));

	StreamCodec<RegistryFriendlyByteBuf, ContainerMenu> STREAM_CODEC = Type.STREAM_CODEC.dispatch(ContainerMenu::getType, Type::streamCodec);

	Type<?> getType();

	MenuConstructor constructor(Container container);

	int columns();

	int rows();

	default int size() {
		return columns() * rows();
	}

	interface Type<C extends ContainerMenu> {

		FixedRegistryAlias<Type<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.CONTAINER_MENU_TYPE);

		Codec<Type<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

		StreamCodec<RegistryFriendlyByteBuf, Type<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONTAINER_MENU_TYPE);

		MapCodec<C> mapCodec();

		StreamCodec<RegistryFriendlyByteBuf, C> streamCodec();

	}

}
