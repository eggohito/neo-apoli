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

	public static final KeyConditionType<AllOfKeyCondition> ALL_OF = registerInternal("all_of", AllOfKeyCondition.CODEC, AllOfKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<AnyOfKeyCondition> ANY_OF = registerInternal("any_of", AnyOfKeyCondition.CODEC, AnyOfKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<CompareKeyCondition> COMPARE = registerInternal("compare", CompareKeyCondition.CODEC, CompareKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<CompareToRangeKeyCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeKeyCondition.CODEC, CompareToRangeKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<ConstantKeyCondition> CONSTANT = registerInternal("constant", ConstantKeyCondition.CODEC, ConstantKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<InvertedKeyCondition> INVERTED = registerInternal("inverted", InvertedKeyCondition.CODEC, InvertedKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<ReferenceKeyCondition> REFERENCE = registerInternal("reference", ReferenceKeyCondition.CODEC, ReferenceKeyCondition.STREAM_CODEC);

	public static final KeyConditionType<IsPressedKeyCondition> IS_PRESSED = registerInternal("is_pressed", IsPressedKeyCondition.CODEC, IsPressedKeyCondition.STREAM_CODEC);
	public static final KeyConditionType<IsSimultaneouslyPressedKeyCondition> IS_SIMULTANEOUSLY_PRESSED = registerInternal("is_simultaneously_pressed", IsSimultaneouslyPressedKeyCondition.CODEC, IsSimultaneouslyPressedKeyCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends KeyCondition> KeyConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends KeyCondition> KeyConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return ConditionTypes.register(id.withPrefix(KeyConditionType.PREFIX), Registry.register(NeoApoliRegistries.KEY_CONDITION_TYPE, id, new KeyConditionType<>(mapCodec, packetCodec)));
	}

}
