package io.github.eggohito.neo_apoli.util.modifier.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import io.github.eggohito.neo_apoli.util.modifier.custom.*;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ModifierTypes {

	public static final ModifierType<MultiplyModifier> MULTIPLY = registerInternal("multiply", MultiplyModifier.CODEC, MultiplyModifier.STREAM_CODEC);
	public static final ModifierType<MultiplyAdditiveModifier> MULTIPLY_ADDITIVE = registerInternal("multiply_additive", MultiplyAdditiveModifier.CODEC, MultiplyAdditiveModifier.STREAM_CODEC);
	public static final ModifierType<MultiplyMultiplicativeModifier> MULTIPLY_MULTIPLICATIVE = registerInternal("multiply_multiplicative", MultiplyMultiplicativeModifier.CODEC, MultiplyMultiplicativeModifier.STREAM_CODEC);
	public static final ModifierType<DivideModifier> DIVIDE = registerInternal("divide", DivideModifier.CODEC, DivideModifier.STREAM_CODEC);
	public static final ModifierType<AddModifier> ADD = registerInternal("add", AddModifier.CODEC, AddModifier.STREAM_CODEC);
	public static final ModifierType<MinModifier> MIN = registerInternal("min", MinModifier.CODEC, MinModifier.STREAM_CODEC);
	public static final ModifierType<MaxModifier> MAX = registerInternal("max", MaxModifier.CODEC, MaxModifier.STREAM_CODEC);
	public static final ModifierType<SetModifier> SET = registerInternal("set", SetModifier.CODEC, SetModifier.STREAM_CODEC);

	public static void registerAll() {
		ModifierType.ALIASES.addPathAlias("multiplication", MULTIPLY);
		ModifierType.ALIASES.addPathAlias("division", DIVIDE);
		ModifierType.ALIASES.addPathAlias("addition", ADD);
	}

	private static <M extends Modifier> ModifierType<M> registerInternal(String path, MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <M extends Modifier> ModifierType<M> register(ResourceLocation id, MapCodec<M> mapCodec, StreamCodec<RegistryFriendlyByteBuf, M> packetCodec) {
		return Registry.register(NeoApoliRegistries.MODIFIER_TYPE, id, new ModifierType<>(mapCodec, packetCodec));
	}

}
