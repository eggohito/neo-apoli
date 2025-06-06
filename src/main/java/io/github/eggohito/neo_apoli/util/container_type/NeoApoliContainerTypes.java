package io.github.eggohito.neo_apoli.util.container_type;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public final class NeoApoliContainerTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<ContainerType> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.CONTAINER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, ContainerType> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.CONTAINER_TYPE);

	//	Presets for generic container screen handlers
	public static final PresetContainerType GENERIC_9X1 = registerInternal("generic_9x1", new PresetContainerType(9, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X2 = registerInternal("generic_9x2", new PresetContainerType(9, 2, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X2, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X3 = registerInternal("generic_9x3", new PresetContainerType(9, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X4 = registerInternal("generic_9x4", new PresetContainerType(9, 4, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X4, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X5 = registerInternal("generic_9x5", new PresetContainerType(9, 5, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X5, syncId, playerInventory, inventory, rows)));
	public static final PresetContainerType GENERIC_9X6 = registerInternal("generic_9x6", new PresetContainerType(9, 6, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, playerInventory, inventory, rows)));

	//	Presets for other container screen handlers
	public static final PresetContainerType GENERIC_3X3 = registerInternal("generic_3x3", new PresetContainerType(3, 3, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new Generic3x3ContainerScreenHandler(syncId, playerInventory, inventory)));
	public static final PresetContainerType HOPPER = registerInternal("hopper", new PresetContainerType(5, 1, (inventory, columns, rows) -> (syncId, playerInventory, player) -> new HopperScreenHandler(syncId, playerInventory)));

	public static void registerAll() {

		ALIASES.addPathAlias("chest", RegistryUtil.getIdPath(NeoApoliRegistries.CONTAINER_TYPE, GENERIC_9X3));
		ALIASES.addPathAlias("double_chest", RegistryUtil.getIdPath(NeoApoliRegistries.CONTAINER_TYPE, GENERIC_9X6));

		//	TODO: Create custom screen handlers for droppers/dispensers for more customization
		ALIASES.addPathAlias("dropper", RegistryUtil.getIdPath(NeoApoliRegistries.CONTAINER_TYPE, GENERIC_3X3));
		ALIASES.addPathAlias("dispenser", RegistryUtil.getIdPath(NeoApoliRegistries.CONTAINER_TYPE, GENERIC_3X3));

	}

	private static <C extends ContainerType> C registerInternal(String path, C containerType) {
		return register(NeoApoli.id(path), containerType);
	}

	public static <C extends ContainerType> C register(Identifier id, C containerType) {
		return Registry.register(NeoApoliRegistries.CONTAINER_TYPE, id, containerType);
	}

}
