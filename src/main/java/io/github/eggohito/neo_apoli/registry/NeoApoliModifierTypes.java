package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.modifier.custom.*;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class NeoApoliModifierTypes {

	public static final Modifier.Type<MultiplyModifier> MULTIPLY = registerInternal("multiply", MultiplyModifier.CODEC, MultiplyModifier.STREAM_CODEC);
	public static final Modifier.Type<MultiplyAdditiveModifier> MULTIPLY_ADDITIVE = registerInternal("multiply_additive", MultiplyAdditiveModifier.CODEC, MultiplyAdditiveModifier.STREAM_CODEC);
	public static final Modifier.Type<MultiplyMultiplicativeModifier> MULTIPLY_MULTIPLICATIVE = registerInternal("multiply_multiplicative", MultiplyMultiplicativeModifier.CODEC, MultiplyMultiplicativeModifier.STREAM_CODEC);
	public static final Modifier.Type<DivideModifier> DIVIDE = registerInternal("divide", DivideModifier.CODEC, DivideModifier.STREAM_CODEC);
	public static final Modifier.Type<AddModifier> ADD = registerInternal("add", AddModifier.CODEC, AddModifier.STREAM_CODEC);
	public static final Modifier.Type<MinModifier> MIN = registerInternal("min", MinModifier.CODEC, MinModifier.STREAM_CODEC);
	public static final Modifier.Type<MaxModifier> MAX = registerInternal("max", MaxModifier.CODEC, MaxModifier.STREAM_CODEC);
	public static final Modifier.Type<SetModifier> SET = registerInternal("set", SetModifier.CODEC, SetModifier.STREAM_CODEC);

	public static void registerAll() {
		Modifier.Type.ALIASES.addPathAlias("multiplication", MULTIPLY);
		Modifier.Type.ALIASES.addPathAlias("division", DIVIDE);
		Modifier.Type.ALIASES.addPathAlias("addition", ADD);
	}

	private static <M extends Modifier> Modifier.Type<M> registerInternal(String path, MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <M extends Modifier> Modifier.Type<M> register(ResourceLocation id, MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> streamCodec) {
		return Registry.register(NeoApoliRegistries.MODIFIER_TYPE, id, new Modifier.Type<>(mapCodec, streamCodec));
	}

}
