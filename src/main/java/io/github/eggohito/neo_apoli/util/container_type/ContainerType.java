package io.github.eggohito.neo_apoli.util.container_type;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.TextAlignment;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuConstructor;
import org.jetbrains.annotations.Range;

public interface ContainerType {

	FixedRegistryAlias<ContainerType> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.CONTAINER_TYPE);

	Codec<ContainerType> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	StreamCodec<RegistryFriendlyByteBuf, ContainerType> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONTAINER_TYPE);

	default TextAlignment textAlignment() {
		return TextAlignment.NONE;
	}

	MenuConstructor create(Container inventory);

	@Range(from = 1, to = Integer.MAX_VALUE)
	int columns();

	@Range(from = 1, to = Integer.MAX_VALUE)
	int rows();

}
