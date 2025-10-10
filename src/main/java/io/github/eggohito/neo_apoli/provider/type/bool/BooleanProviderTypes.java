package io.github.eggohito.neo_apoli.provider.type.bool;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConditionResultBooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.IfElseBooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.IfElseListBooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.NbtBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BooleanProviderTypes {

	public static final BooleanProviderType<ConstantBooleanProvider> CONSTANT = registerInternal("constant", ConstantBooleanProvider.CODEC, ConstantBooleanProvider.PACKET_CODEC);
	public static final BooleanProviderType<IfElseBooleanProvider> IF_ELSE = registerInternal("if_else", IfElseBooleanProvider.CODEC, IfElseBooleanProvider.PACKET_CODEC);
	public static final BooleanProviderType<IfElseListBooleanProvider> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListBooleanProvider.CODEC, IfElseListBooleanProvider.PACKET_CODEC);
	public static final BooleanProviderType<NbtBooleanProvider> NBT = registerInternal("nbt", NbtBooleanProvider.CODEC, NbtBooleanProvider.PACKET_CODEC);

	public static final BooleanProviderType<ConditionResultBooleanProvider> CONDITION_RESULT = registerInternal("condition_result", ConditionResultBooleanProvider.CODEC, ConditionResultBooleanProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends BooleanProvider> BooleanProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends BooleanProvider> BooleanProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.BOOLEAN_PROVIDER_TYPE, id, new BooleanProviderType<>(mapCodec, packetCodec));
	}

}
