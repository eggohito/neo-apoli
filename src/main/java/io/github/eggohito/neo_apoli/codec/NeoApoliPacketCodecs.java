package io.github.eggohito.neo_apoli.codec;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Set;

public class NeoApoliPacketCodecs {

	public static final PacketCodec<ByteBuf, Set<Identifier>> MUTABLE_IDENTIFIER_SET = PacketCodecs.collection(ObjectOpenHashSet::new, PacketCodecs.STRING.xmap(Identifier::of, Identifier::toString));

}
