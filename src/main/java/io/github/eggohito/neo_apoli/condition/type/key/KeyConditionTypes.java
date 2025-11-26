package io.github.eggohito.neo_apoli.condition.type.key;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.key.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class KeyConditionTypes extends ConditionTypes {

	public static final KeyConditionType<AllOfKeyCondition> ALL_OF = registerMetaInternal("all_of", AllOfKeyCondition.CODEC, AllOfKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<AnyOfKeyCondition> ANY_OF = registerMetaInternal("any_of", AnyOfKeyCondition.CODEC, AnyOfKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<CompareKeyCondition> COMPARE = registerMetaInternal("compare", CompareKeyCondition.CODEC, CompareKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<CompareToRangeKeyCondition> COMPARE_TO_RANGE = registerMetaInternal("compare_to_range", CompareToRangeKeyCondition.CODEC, CompareToRangeKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<ConstantKeyCondition> CONSTANT = registerMetaInternal("constant", ConstantKeyCondition.CODEC, ConstantKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<InvertedKeyCondition> INVERTED = registerMetaInternal("inverted", InvertedKeyCondition.CODEC, InvertedKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<ReferenceKeyCondition> REFERENCE = registerMetaInternal("reference", ReferenceKeyCondition.CODEC, ReferenceKeyCondition.STREAM_CODEC);

	public static final KeyConditionType<IsPressedKeyCondition> IS_PRESSED = registerInternal("is_pressed", IsPressedKeyCondition.CODEC, IsPressedKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<IsSimultaneouslyPressedKeyCondition> IS_SIMULTANEOUSLY_PRESSED = registerInternal("is_simultaneously_pressed", IsSimultaneouslyPressedKeyCondition.CODEC, IsSimultaneouslyPressedKeyCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends KeyCondition> KeyConditionType<C> registerMetaInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return registerMeta(NeoApoli.id(path), mapCodec, streamCodec);
	}

	private static <C extends KeyCondition> KeyConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends KeyCondition> KeyConditionType<C> registerMeta(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.KEY_CONDITION_TYPE, id, new KeyConditionType<>(mapCodec, streamCodec));
	}

	public static <C extends KeyCondition> KeyConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		ResourceLocation prefixedId = id.withPrefix(KeyConditionType.PREFIX);
		return ConditionTypes.register(prefixedId, registerMeta(prefixedId, mapCodec, streamCodec));
	}

}
