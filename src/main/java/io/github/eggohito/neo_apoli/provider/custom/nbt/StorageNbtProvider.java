package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StorageNbtProvider(ResourceLocation id) implements NbtProvider {

	public static final MapCodec<StorageNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("id").forGetter(StorageNbtProvider::id)
	).apply(instance, StorageNbtProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, StorageNbtProvider> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, StorageNbtProvider::id,
		StorageNbtProvider::new
	);

	@Override
	public @NotNull NbtProviderType<?> getType() {
		return NbtProviderTypes.STORAGE;
	}

	@Override
	public @NotNull Tag nextTag(Context context) {
		return ((CommandStorageHolder) context.level()).neo_apoli$getStorage(this.id());
	}

}
