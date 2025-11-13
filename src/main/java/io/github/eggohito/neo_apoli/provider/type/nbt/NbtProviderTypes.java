package io.github.eggohito.neo_apoli.provider.type.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.nbt.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NbtProviderTypes {

	public static final NbtProviderType<BlockEntityNbtProvider> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityNbtProvider.CODEC, BlockEntityNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<ChoiceNbtProvider> CHOICE = registerInternal("choice", ChoiceNbtProvider.CODEC, ChoiceNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<ConditionalNbtProvider> CONDITIONAL = registerInternal("conditional", ConditionalNbtProvider.CODEC, ConditionalNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<ConstantNbtProvider> CONSTANT = registerInternal("constant", ConstantNbtProvider.CODEC, ConstantNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<EntityNbtProvider> ENTITY = registerInternal("entity", EntityNbtProvider.CODEC, EntityNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<ItemNbtProvider> ITEM = registerInternal("item", ItemNbtProvider.CODEC, ItemNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<StorageNbtProvider> STORAGE = registerInternal("storage", StorageNbtProvider.CODEC, StorageNbtProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends NbtProvider> NbtProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NbtProvider> NbtProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NBT_PROVIDER_TYPE, id, new NbtProviderType<>(mapCodec, packetCodec));
	}

}
