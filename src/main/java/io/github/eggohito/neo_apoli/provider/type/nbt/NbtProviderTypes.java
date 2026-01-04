package io.github.eggohito.neo_apoli.provider.type.nbt;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.nbt.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NbtProviderTypes {

	public static final NbtProviderType<BlockEntityNbtProvider> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityNbtProvider.CODEC, BlockEntityNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ChoiceNbtProvider> CHOICE = registerInternal("choice", ChoiceNbtProvider.CODEC, ChoiceNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ConditionalNbtProvider> CONDITIONAL = registerInternal("conditional", ConditionalNbtProvider.CODEC, ConditionalNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ConstantNbtProvider> CONSTANT = registerInternal("constant", ConstantNbtProvider.CODEC, ConstantNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<EntityNbtProvider> ENTITY = registerInternal("entity", EntityNbtProvider.CODEC, EntityNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ItemNbtProvider> ITEM = registerInternal("item", ItemNbtProvider.CODEC, ItemNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<PowerNbtProvider> POWER = registerInternal("power", PowerNbtProvider.CODEC, PowerNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<StorageNbtProvider> STORAGE = registerInternal("storage", StorageNbtProvider.CODEC, StorageNbtProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends NbtProvider> NbtProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NbtProvider> NbtProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NBT_PROVIDER_TYPE, id, new NbtProviderType<>(mapCodec, packetCodec));
	}

}
