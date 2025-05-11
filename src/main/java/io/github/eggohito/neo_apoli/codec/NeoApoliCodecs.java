package io.github.eggohito.neo_apoli.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.util.HandProperty;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ValueLists;
import net.minecraft.world.LightType;

import java.util.Locale;
import java.util.Set;
import java.util.function.Supplier;

public class NeoApoliCodecs {

	public static final Codec<Set<Identifier>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = Identifier.CODEC.listOf(1, Integer.MAX_VALUE).xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Codec<Hand> HAND = HandProperty.CODEC.xmap(HandProperty::get, HandProperty::fromHand);

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

	public static final Codec<LightType> LIGHT_TYPE = Codecs.orCompressed(
		Codec.stringResolver(Enum::name, str -> LightType.valueOf(str.toUpperCase(Locale.ROOT))),
		Codecs.rawIdChecked(LightType::ordinal, ValueLists.createIndexToValueFunction(LightType::ordinal, LightType.values(), ValueLists.OutOfBoundsHandling.WRAP), -1)
	);

	public static <A> MapCodec<A> lazyMap(String name, Supplier<MapCodec<A>> delegate) {
		return MapCodec.recursive(name, self -> delegate.get());
	}

	public static <A> MapCodec<A> lazyMap(Supplier<MapCodec<A>> delegate) {
		return lazyMap(delegate.toString(), delegate);
	}

}
