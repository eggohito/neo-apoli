package io.github.eggohito.neo_apoli.provider.type.box;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.BoxProvider;
import io.github.eggohito.neo_apoli.provider.custom.box.DynamicBoxProvider;
import io.github.eggohito.neo_apoli.provider.custom.box.EntityBoxProvider;
import io.github.eggohito.neo_apoli.provider.meta.box.ConstantBoxProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BoxProviderTypes {

	public static final BoxProviderType<ConstantBoxProvider> CONSTANT = registerInternal("constant", ConstantBoxProvider.CODEC, ConstantBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<DynamicBoxProvider> DYNAMIC = registerInternal("dynamic", DynamicBoxProvider.CODEC, DynamicBoxProvider.PACKET_CODEC);
	public static final BoxProviderType<EntityBoxProvider> ENTITY = registerInternal("entity", EntityBoxProvider.CODEC, EntityBoxProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <B extends BoxProvider> BoxProviderType<B> registerInternal(String path, MapCodec<B> mapCodec, PacketCodec<RegistryByteBuf, B> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <B extends BoxProvider> BoxProviderType<B> register(Identifier id, MapCodec<B> mapCodec, PacketCodec<RegistryByteBuf, B> packetCodec) {
		return Registry.register(NeoApoliRegistries.BOX_PROVIDER_TYPE, id, new BoxProviderType<>(mapCodec, packetCodec));
	}

}
