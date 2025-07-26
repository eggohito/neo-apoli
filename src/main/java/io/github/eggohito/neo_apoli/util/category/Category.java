package io.github.eggohito.neo_apoli.util.category;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public interface Category<A> {

	RegistryKey<? extends Registry<A>> registryRef();

	PacketCodec<RegistryByteBuf, A> packetCodec();

	Codec<A> codec();

	MapCodec<A> mapCodec();

}
