package io.github.eggohito.neo_apoli.condition.type.fluid;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.fluid.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class FluidConditionTypes extends ConditionTypes {

	public static final FluidConditionType<AllOfFluidCondition> ALL_OF = registerInternal("all_of", AllOfFluidCondition.CODEC, AllOfFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<AnyOfFluidCondition> ANY_OF = registerInternal("any_of", AnyOfFluidCondition.CODEC, AnyOfFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<CompareFluidCondition> COMPARE = registerInternal("compare", CompareFluidCondition.CODEC, CompareFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<CompareToRangeFluidCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeFluidCondition.CODEC, CompareToRangeFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<ConstantFluidCondition> CONSTANT = registerInternal("constant", ConstantFluidCondition.CODEC, ConstantFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<DynamicFluidCondition> DYNAMIC = registerInternal("dynamic", DynamicFluidCondition.CODEC, DynamicFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<InvertedFluidCondition> INVERTED = registerInternal("inverted", InvertedFluidCondition.CODEC, InvertedFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<ReferenceFluidCondition> REFERENCE = registerInternal("reference", ReferenceFluidCondition.CODEC, ReferenceFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<TestWorldFluidCondition> TEST_WORLD = registerInternal("test_world", TestWorldFluidCondition.CODEC, TestWorldFluidCondition.STREAM_CODEC);

	public static final FluidConditionType<IsOfFluidCondition> IS_OF = registerInternal("is_of", IsOfFluidCondition.CODEC, IsOfFluidCondition.STREAM_CODEC);
	public static final FluidConditionType<IsInTagFluidCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagFluidCondition.CODEC, IsInTagFluidCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends FluidCondition> FluidConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends FluidCondition> FluidConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(FluidConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, Registry.register(NeoApoliRegistries.FLUID_CONDITION_TYPE, prefixedId, new FluidConditionType<>(mapCodec, streamCodec)));
	}

}
