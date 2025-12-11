package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum HudRenderPhase {

	BELOW_HUD,
	ABOVE_HUD;

	public static final Codec<HudRenderPhase> CODEC = CodecUtil.enumType(HudRenderPhase.class);
	public static final StreamCodec<ByteBuf, HudRenderPhase> STREAM_CODEC = StreamCodecUtil.enumType(HudRenderPhase.class);

}
