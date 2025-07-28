package io.github.eggohito.neo_apoli.provider.type.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.BlockEntityNbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.EntityNbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.ItemNbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.StorageNbtProvider;
import io.github.eggohito.neo_apoli.provider.meta.nbt.ConstantNbtProvider;
import io.github.eggohito.neo_apoli.provider.meta.nbt.IfElseListNbtProvider;
import io.github.eggohito.neo_apoli.provider.meta.nbt.IfElseNbtProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class NbtProviderTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<NbtProviderType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.NBT_PROVIDER_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, NbtProviderType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.NBT_PROVIDER_TYPE);

	public static final NbtProviderType<ConstantNbtProvider> CONSTANT = registerInternal("constant", ConstantNbtProvider.CODEC, ConstantNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<IfElseListNbtProvider> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListNbtProvider.CODEC, IfElseListNbtProvider.PACKET_CODEC);
	public static final NbtProviderType<IfElseNbtProvider> IF_ELSE = registerInternal("if_else", IfElseNbtProvider.CODEC, IfElseNbtProvider.PACKET_CODEC);

	public static final NbtProviderType<BlockEntityNbtProvider> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityNbtProvider.CODEC, BlockEntityNbtProvider.PACKET_CODEC);
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
