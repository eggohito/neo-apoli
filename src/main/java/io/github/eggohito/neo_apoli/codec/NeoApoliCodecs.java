package io.github.eggohito.neo_apoli.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.util.EntityParameter;
import io.github.eggohito.neo_apoli.util.HandProperty;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.function.ValueLists;
import net.minecraft.world.LightType;
import net.minecraft.world.explosion.Explosion;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

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

	public static final Codec<LightType> LIGHT_TYPE = enumType(LightType.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static final Codec<Map<EntityParameter, EntityParameter>> ENTITY_PARAMETER_MAP = Codec.unboundedMap(EntityParameter.CODEC, EntityParameter.CODEC);

	public static final Codec<Explosion.DestructionType> DESTRUCTION_TYPE = enumType(Explosion.DestructionType.class, ValueLists.OutOfBoundsHandling.WRAP);

	public static <E extends Enum<E>> Codec<E> enumType(Class<E> clazz, ValueLists.OutOfBoundsHandling outOfBoundsHandling) {

		ToIntFunction<E> toOrdinal = Enum::ordinal;

		Codec<E> byString = Codec.stringResolver(Enum::name, name -> Enum.valueOf(clazz, name.toUpperCase(Locale.ROOT)));
		Codec<E> byId = Codecs.rawIdChecked(toOrdinal, ValueLists.createIndexToValueFunction(toOrdinal, clazz.getEnumConstants(), outOfBoundsHandling), -1);

		return Codecs.orCompressed(byString, byId);

	}

}
