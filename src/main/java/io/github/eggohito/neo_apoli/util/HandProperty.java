package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;

import java.util.function.Supplier;

public enum HandProperty implements Supplier<InteractionHand>, StringRepresentable {

	MAINHAND {
		@Override
		public InteractionHand get() {
			return InteractionHand.MAIN_HAND;
		}

		@Override
		public String getSerializedName() {
			return "mainhand";
		}

	},

	OFFHAND {
		@Override
		public InteractionHand get() {
			return InteractionHand.OFF_HAND;
		}

		@Override
		public String getSerializedName() {
			return "offhand";
		}

	};

	public static final Codec<HandProperty> CODEC = CodecUtil.enumType(HandProperty.class);
	public static final StreamCodec<ByteBuf, HandProperty> STREAM_CODEC = StreamCodecUtil.enumType(HandProperty.class);

	public static HandProperty fromHand(InteractionHand hand) {
		return switch (hand) {
			case MAIN_HAND ->
				MAINHAND;
			case OFF_HAND ->
				OFFHAND;
		};
	}

}
