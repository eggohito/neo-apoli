package io.github.eggohito.neo_apoli.codec;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.internal.LazilyParsedNumber;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.HandProperty;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.explosion.Explosion;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class NeoApoliPacketCodecs {

	public static final PacketCodec<ByteBuf, Hand> HAND = HandProperty.PACKET_CODEC.xmap(HandProperty::get, HandProperty::fromHand);

	public static final PacketCodec<ByteBuf, List<Hand>> HANDS = PacketCodecs.collection(ObjectArrayList::new, HAND);

	public static final PacketCodec<ByteBuf, EnumSet<Hand>> HAND_SET = HANDS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final PacketCodec<PacketByteBuf, Number> NUMBER = new PacketCodec<>() {

		@Override
		public Number decode(PacketByteBuf buf) {
			byte type = buf.readByte();
			return switch (type) {
				case 0 ->
					buf.readByte();
				case 1 ->
					buf.readDouble();
				case 2 ->
					buf.readFloat();
				case 3 ->
					buf.readInt();
				case 4 ->
					buf.readLong();
				case 5 ->
					buf.readShort();
				case 6 ->
					new LazilyParsedNumber(buf.readString());
				default ->
					throw new IllegalArgumentException("Unsupported number type: " + type);
			};
		}

		@Override
		public void encode(PacketByteBuf buf, Number value) {
			switch (value) {
				case Byte b -> {
					buf.writeByte(0);
					buf.writeByte(b);
				}
				case Double d -> {
					buf.writeByte(1);
					buf.writeDouble(d);
				}
				case Float f -> {
					buf.writeByte(2);
					buf.writeFloat(f);
				}
				case Integer i -> {
					buf.writeByte(3);
					buf.writeInt(i);
				}
				case Long l -> {
					buf.writeByte(4);
					buf.writeLong(l);
				}
				case Short s -> {
					buf.writeByte(5);
					buf.writeShort(s);
				}
				case LazilyParsedNumber n -> {
					buf.writeByte(6);
					buf.writeString(n.toString());
				}
				default ->
					throw new IllegalArgumentException("Unsupported number: " + value);
			}
		}

	};

	public static final PacketCodec<ByteBuf, NbtPathArgumentType.NbtPath> NBT_PATH = PacketCodecs.unlimitedCodec(NbtPathArgumentType.NbtPath.CODEC);

	public static final PacketCodec<ByteBuf, LightType> LIGHT_TYPE = enumType(LightType.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static final PacketCodec<ByteBuf, List<Direction>> DIRECTIONS = PacketCodecs.collection(ObjectArrayList::new, Direction.PACKET_CODEC);

	public static final PacketCodec<ByteBuf, EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final PacketCodec<ByteBuf, Direction.Axis> AXIS = enumType(Direction.Axis.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static final PacketCodec<ByteBuf, Map<EntityParameter, EntityParameter>> ENTITY_PARAMETER_MAP = PacketCodecs.map(Object2ObjectOpenHashMap::new, EntityParameter.PACKET_CODEC, EntityParameter.PACKET_CODEC);

	public static final PacketCodec<ByteBuf, Explosion.DestructionType> DESTRUCTION_TYPE = enumType(Explosion.DestructionType.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static final PacketCodec<PacketByteBuf, ActionResult> ACTION_RESULT = mapped(builder -> builder
		.put("success", ActionResult.SUCCESS)
		.put("success_server", ActionResult.SUCCESS_SERVER)
		.put("consume", ActionResult.CONSUME)
		.put("fail", ActionResult.FAIL)
		.put("pass", ActionResult.PASS));

	public static <B extends ByteBuf, A> PacketCodec<B, A> lazy(String name, Supplier<PacketCodec<B, A>> delegate) {
		return new PacketCodec<>() {

			@Override
			public A decode(B buf) {
				return delegate.get().decode(buf);
			}

			@Override
			public void encode(B buf, A value) {
				delegate.get().encode(buf, value);
			}

			@Override
			public String toString() {
				return "RecursivePacketCodec[" + name + "]";
			}

		};
	}

	public static <B extends ByteBuf, A> PacketCodec<B, A> lazy(Supplier<PacketCodec<B, A>> delegate) {
		return lazy(delegate.toString(), delegate);
	}

	public static <B extends ByteBuf, E extends Enum<E>> PacketCodec<B, E> enumType(Class<E> clazz, ValueLists.OutOfBoundsHandling outOfBoundsHandling) {
		ToIntFunction<E> toOrdinal = Enum::ordinal;
		return PacketCodecs.indexed(ValueLists.createIndexToValueFunction(toOrdinal, clazz.getEnumConstants(), outOfBoundsHandling), toOrdinal).cast();
	}

	public static <B extends PacketByteBuf, E> PacketCodec<B, E> mapped(Consumer<ImmutableBiMap.Builder<String, E>> consumer) {

		ImmutableBiMap.Builder<String, E> builder = ImmutableBiMap.builder();
		consumer.accept(builder);

		return mapped(builder.build());

	}

	public static <B extends PacketByteBuf, E> PacketCodec<B, E> mapped(BiMap<String, E> map) {
		return mapped(Suppliers.memoize(() -> map));
	}

	public static <B extends PacketByteBuf, E> PacketCodec<B, E> mapped(Supplier<BiMap<String, E>> supplier) {
		return new PacketCodec<>() {

			@Override
			public E decode(B buf) {

				BiMap<String, E> mappedValues = supplier.get();
				E value = mappedValues.get(buf.readString());

				if (value != null) {
					return value;
				}

				else {
					throw new IllegalArgumentException("Expected value to be any of " + String.join(", ", mappedValues.keySet()));
				}

			}

			@Override
			public void encode(B buf, E value) {

				BiMap<String, E> mappedValues = supplier.get();
				String key = mappedValues.inverse().get(value);

				if (key != null) {
					buf.writeString(key);
				}

				else {
					throw new IllegalArgumentException("Value " + value + " is not associated with any keys!");
				}

			}

		};
	}

}
