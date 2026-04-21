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

	public static final NbtProviderType<BlockEntityNbtProvider> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityNbtProvider.MAP_CODEC, BlockEntityNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ConditionalNbtProvider> CONDITIONAL = registerInternal("conditional", ConditionalNbtProvider.MAP_CODEC, ConditionalNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ConstantNbtProvider> CONSTANT = registerInternal("constant", ConstantNbtProvider.MAP_CODEC, ConstantNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<EntityNbtProvider> ENTITY = registerInternal("entity", EntityNbtProvider.MAP_CODEC, EntityNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<ItemNbtProvider> ITEM = registerInternal("item", ItemNbtProvider.MAP_CODEC, ItemNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<PowerNbtProvider> POWER = registerInternal("power", PowerNbtProvider.MAP_CODEC, PowerNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<StorageNbtProvider> STORAGE = registerInternal("storage", StorageNbtProvider.MAP_CODEC, StorageNbtProvider.STREAM_CODEC);
	public static final NbtProviderType<SwitchNbtProvider> SWITCH = registerInternal("switch", SwitchNbtProvider.MAP_CODEC, SwitchNbtProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends NbtProvider> NbtProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <P extends NbtProvider> NbtProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return Registry.register(NeoApoliRegistries.NBT_PROVIDER_TYPE, id, new NbtProviderType<>(mapCodec, streamCodec));
	}

}
