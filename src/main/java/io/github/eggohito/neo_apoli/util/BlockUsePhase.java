package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.EnumSet;

//	TODO: Decide whether to integrate "using an item on block" to the `block_interact` power type, or implement it
//		  as a different power type
public enum BlockUsePhase implements StringRepresentable {

	BLOCK("block"),
	BLOCK_WITH_ITEM("block_with_item");
//	ITEM_ON_BLOCK("item_on_block");

	public static final Codec<BlockUsePhase> CODEC = CodecUtil.enumType(BlockUsePhase.class);
	public static final StreamCodec<ByteBuf, BlockUsePhase> STREAM_CODEC = StreamCodecUtil.enumType(BlockUsePhase.class);

	public static final Codec<EnumSet<BlockUsePhase>> SET_CODEC = CODEC.listOf().xmap(EnumSet::copyOf, ObjectArrayList::new);
	public static final StreamCodec<ByteBuf, EnumSet<BlockUsePhase>> SET_STREAM_CODEC = ByteBufCodecs.collection(ObjectArrayList::new, STREAM_CODEC).map(EnumSet::copyOf, ObjectArrayList::new);

	final String stringForm;
	BlockUsePhase(String stringForm) {
		this.stringForm = stringForm;
	}

	@Override
	public String getSerializedName() {
		return stringForm;
	}

}
