package io.github.eggohito.neo_apoli.util.category;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public interface Category<A> {

	RegistryKey<? extends Registry<A>> registryRef();

	Codec<A> baseCodec();

	default Codec<A> entryCodec() {
		return this.baseCodec();
	}

	PacketCodec<RegistryByteBuf, A> basePacketCodec();

}
