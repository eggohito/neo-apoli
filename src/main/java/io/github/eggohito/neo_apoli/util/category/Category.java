package io.github.eggohito.neo_apoli.util.category;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.regex.Pattern;

public interface Category<A> {

	Pattern NAME_PATTERN = Pattern.compile("[^ a-z0-9/_]");

	String directory();

	Codec<A> codec();

	PacketCodec<RegistryByteBuf, A> packetCodec();

}
