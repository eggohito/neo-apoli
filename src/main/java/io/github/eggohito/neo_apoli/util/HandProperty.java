package io.github.eggohito.neo_apoli.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;

import java.util.function.IntFunction;
import java.util.function.Supplier;

public enum HandProperty implements Supplier<Hand>, StringIdentifiable {

	MAINHAND {
		@Override
		public Hand get() {
			return Hand.MAIN_HAND;
		}

		@Override
		public String asString() {
			return "mainhand";
		}

	},

	OFFHAND {
		@Override
		public Hand get() {
			return Hand.OFF_HAND;
		}

		@Override
		public String asString() {
			return "offhand";
		}

	};

	private static final IntFunction<HandProperty> FROM_ORDINAL = ValueLists.createIndexToValueFunction(HandProperty::ordinal, HandProperty.values(), ValueLists.OutOfBoundsHandling.WRAP);

	public static final Codec<HandProperty> CODEC = StringIdentifiable.createBasicCodec(HandProperty::values);
	public static final PacketCodec<ByteBuf, HandProperty> PACKET_CODEC = PacketCodecs.indexed(FROM_ORDINAL, HandProperty::ordinal);

	public static HandProperty fromHand(Hand hand) {
		return switch (hand) {
			case MAIN_HAND ->
				MAINHAND;
			case OFF_HAND ->
				OFFHAND;
		};
	}

}
