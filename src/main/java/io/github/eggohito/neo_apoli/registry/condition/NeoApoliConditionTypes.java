package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliConditionTypes {

	public static final Condition.Type<AllOfCondition> ALL_OF = registerInternal("all_of", AllOfCondition.MAP_CODEC, AllOfCondition.STREAM_CODEC);
	public static final Condition.Type<AnyOfCondition> ANY_OF = registerInternal("any_of", AnyOfCondition.MAP_CODEC, AnyOfCondition.STREAM_CODEC);
	public static final Condition.Type<CompareCondition> COMPARE = registerInternal("compare", CompareCondition.MAP_CODEC, CompareCondition.STREAM_CODEC);
	public static final Condition.Type<CompareToRangeCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeCondition.MAP_CODEC, CompareToRangeCondition.STREAM_CODEC);
	public static final Condition.Type<ConstantCondition> CONSTANT = registerInternal("constant", ConstantCondition.MAP_CODEC, ConstantCondition.STREAM_CODEC);
	public static final Condition.Type<DynamicCondition> DYNAMIC = registerInternal("dynamic", DynamicCondition.MAP_CODEC, DynamicCondition.STREAM_CODEC);
	public static final Condition.Type<InvertedCondition> INVERTED = registerInternal("inverted", InvertedCondition.MAP_CODEC, InvertedCondition.STREAM_CODEC);
	public static final Condition.Type<ReferenceCondition> REFERENCE = registerInternal("reference", ReferenceCondition.MAP_CODEC, ReferenceCondition.STREAM_CODEC);
	public static final Condition.Type<TestEntityCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityCondition.MAP_CODEC, TestEntityCondition.STREAM_CODEC);
	public static final Condition.Type<TestWorldCondition> TEST_WORLD = registerInternal("test_world", TestWorldCondition.MAP_CODEC, TestWorldCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends Condition> Condition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends Condition> Condition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {

		var type = new Condition.Type<C>() {

			@Override
			public Condition.Kind<?> kind() {
				return Condition.Kind.INSTANCE;
			}

			@Override
			public MapCodec<C> mapCodec() {
				return mapCodec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, C> streamCodec() {
				return streamCodec;
			}

		};

		return Registry.register(NeoApoliRegistries.CONDITION_TYPE, id, type);

	}

}
