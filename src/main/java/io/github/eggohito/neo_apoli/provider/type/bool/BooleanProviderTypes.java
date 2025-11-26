package io.github.eggohito.neo_apoli.provider.type.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.bool.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class BooleanProviderTypes {

	public static final BooleanProviderType<ChoiceBooleanProvider> CHOICE = registerInternal("choice", ChoiceBooleanProvider.CODEC, ChoiceBooleanProvider.STREAM_CODEC);
	public static final BooleanProviderType<ConditionalBooleanProvider> CONDITIONAL = registerInternal("conditional", ConditionalBooleanProvider.CODEC, ConditionalBooleanProvider.STREAM_CODEC);
	public static final BooleanProviderType<ConditionResultBooleanProvider> CONDITION_RESULT = registerInternal("condition_result", ConditionResultBooleanProvider.CODEC, ConditionResultBooleanProvider.STREAM_CODEC);
	public static final BooleanProviderType<ConstantBooleanProvider> CONSTANT = registerInternal("constant", ConstantBooleanProvider.CODEC, ConstantBooleanProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends BooleanProvider> BooleanProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends BooleanProvider> BooleanProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, id, new BooleanProviderType<>(mapCodec, packetCodec));
	}

}
