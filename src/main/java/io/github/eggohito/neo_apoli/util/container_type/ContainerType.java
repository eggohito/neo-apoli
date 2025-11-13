package io.github.eggohito.neo_apoli.util.container_type;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.TextAlignment;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.ScreenHandlerFactory;
import org.jetbrains.annotations.Range;

public interface ContainerType {

	RegistryFixedAlias<ContainerType> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.CONTAINER_TYPE);

	Codec<ContainerType> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	PacketCodec<RegistryByteBuf, ContainerType> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.CONTAINER_TYPE);

	default TextAlignment textAlignment() {
		return TextAlignment.NONE;
	}

	ScreenHandlerFactory create(Inventory inventory);

	@Range(from = 1, to = Integer.MAX_VALUE)
	int columns();

	@Range(from = 1, to = Integer.MAX_VALUE)
	int rows();

}
