package io.github.eggohito.neo_apoli.codec;

import io.github.eggohito.neo_apoli.util.HandProperty;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.loot.context.LootContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.Set;

public class NeoApoliPacketCodecs {

	public static final PacketCodec<ByteBuf, Set<Identifier>> MUTABLE_IDENTIFIER_SET = PacketCodecs.collection(ObjectOpenHashSet::new, PacketCodecs.STRING.xmap(Identifier::of, Identifier::toString));

	public static final PacketCodec<ByteBuf, Hand> HAND = HandProperty.PACKET_CODEC.xmap(HandProperty::get, HandProperty::fromHand);

	public static final PacketCodec<PacketByteBuf, LootContext.EntityTarget> ENTITY_TARGET = PacketCodec.ofStatic(PacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(LootContext.EntityTarget.class));

}
