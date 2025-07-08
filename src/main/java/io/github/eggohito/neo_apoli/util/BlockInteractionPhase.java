package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;

import java.util.EnumSet;

//	TODO: Decide whether to integrate "using an item on block" to the `block_interact` power type, or implement it
//		  as a different power type
public enum BlockInteractionPhase implements StringIdentifiable {

	BLOCK("block"),
	BLOCK_WITH_ITEM("block_with_item");
//	ITEM_ON_BLOCK("item_on_block");

	public static final Codec<BlockInteractionPhase> CODEC = CodecUtil.enumType(BlockInteractionPhase.class);
	public static final PacketCodec<ByteBuf, BlockInteractionPhase> PACKET_CODEC = PacketCodecUtil.enumType(BlockInteractionPhase.class);

	public static final Codec<EnumSet<BlockInteractionPhase>> SET_CODEC = CODEC.listOf().xmap(EnumSet::copyOf, ObjectArrayList::new);
	public static final PacketCodec<ByteBuf, EnumSet<BlockInteractionPhase>> SET_PACKET_CODEC = PacketCodecs.collection(ObjectArrayList::new, PACKET_CODEC).xmap(EnumSet::copyOf, ObjectArrayList::new);

	final String stringForm;
	BlockInteractionPhase(String stringForm) {
		this.stringForm = stringForm;
	}

	@Override
	public String asString() {
		return stringForm;
	}

}
