package io.github.eggohito.neo_apoli.codec;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.mixin.access.StringNbtReaderAccessor;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.HandProperty;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.explosion.Explosion;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class NeoApoliCodecs {

	public static final Codec<Set<Identifier>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = Identifier.CODEC.listOf(1, Integer.MAX_VALUE).xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Codec<Hand> HAND = HandProperty.CODEC.xmap(HandProperty::get, HandProperty::fromHand);

	public static final Codec<List<Hand>> HANDS = HAND.listOf();

	public static final Codec<EnumSet<Hand>> HAND_SET = HANDS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<Number> NUMBER = new Codec<>() {

		@Override
		public <I> DataResult<Pair<Number, I>> decode(DynamicOps<I> ops, I input) {
			return ops.getNumberValue(input).map(number -> Pair.of(number, input));
		}

		@Override
		public <I> DataResult<I> encode(Number number, DynamicOps<I> ops, I prefix) {
			return DataResult.success(ops.createNumeric(number));
		}

	};

	public static final Codec<LightType> LIGHT_TYPE = enumType(LightType.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static final Codec<Map<EntityParameter, EntityParameter>> ENTITY_PARAMETER_MAP = Codec.unboundedMap(EntityParameter.CODEC, EntityParameter.CODEC);

	public static final Codec<Explosion.DestructionType> DESTRUCTION_TYPE = enumType(Explosion.DestructionType.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static final Codec<List<Direction>> DIRECTIONS = Direction.CODEC.listOf();

	public static final Codec<EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<ActionResult> ACTION_RESULT = mapped(builder -> builder
		.put("success", ActionResult.SUCCESS)
		.put("success_server", ActionResult.SUCCESS_SERVER)
		.put("consume", ActionResult.CONSUME)
		.put("fail", ActionResult.FAIL)
		.put("pass", ActionResult.PASS));

	public static final Codec<NbtElement> STRINGIFIED_NBT = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(StringNbtReaderAccessor.getDefaultReader().read(str));
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Error parsing string NBT: " + e.getMessage());
			}

		},
		nbtElement -> {

			StringNbtWriter nbtWriter = new StringNbtWriter();
			nbtElement.accept(nbtWriter);

			return nbtWriter.getString();

		}
	);

	public static final Codec<NbtElement> NBT_ELEMENT = Codec.PASSTHROUGH.xmap(
		dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(),
		nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement)
	);

	public static final Codec<NbtElement> REGULAR_OR_STRINGIFIED_NBT_ELEMENT = new MultiAlternativeCodec<>(NBT_ELEMENT, STRINGIFIED_NBT);

	public static <E extends Enum<E>> Codec<E> enumType(Class<E> clazz, ValueLists.OutOfBoundsHandling outOfBoundsHandling) {

		ToIntFunction<E> toOrdinal = Enum::ordinal;

		Codec<E> byString = Codec.stringResolver(Enum::name, name -> Enum.valueOf(clazz, name.toUpperCase(Locale.ROOT)));
		Codec<E> byId = Codecs.rawIdChecked(toOrdinal, ValueLists.createIndexToValueFunction(toOrdinal, clazz.getEnumConstants(), outOfBoundsHandling), -1);

		return Codecs.orCompressed(byString, byId);

	}

	public static <E> Codec<E> mapped(Consumer<ImmutableBiMap.Builder<String, E>> consumer) {

		ImmutableBiMap.Builder<String, E> builder = ImmutableBiMap.builder();
		consumer.accept(builder);

		return mapped(builder.build());

	}

	public static <E> Codec<E> mapped(BiMap<String, E> map) {
		return mapped(Suppliers.memoize(() -> map));
	}

	public static <E> Codec<E> mapped(Supplier<BiMap<String, E>> supplier) {
		return Codec.STRING.flatXmap(
			str -> {

				BiMap<String, E> mappedValues = supplier.get();
				E value = mappedValues.get(str);

				if (value != null) {
					return DataResult.success(value);
				}

				else {
					return DataResult.error(() -> "Expected value to be any of " + String.join(", ", mappedValues.keySet()));
				}

			},
			e -> {

				BiMap<String, E> mappedValues = supplier.get();
				String key = mappedValues.inverse().get(e);

				if (key != null) {
					return DataResult.success(key);
				}

				else {
					return DataResult.error(() -> "Value " + e + " is not associated to any keys!");
				}

			}
		);
	}

}
