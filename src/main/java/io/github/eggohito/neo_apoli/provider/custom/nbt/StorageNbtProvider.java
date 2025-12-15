package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.DynamicResourceLocation;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record StorageNbtProvider(ResourceLocation id) implements NbtProvider {

	public static final MapCodec<StorageNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DynamicResourceLocation.CODEC.fieldOf("id").forGetter(StorageNbtProvider::id)
	).apply(instance, StorageNbtProvider::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, StorageNbtProvider> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, StorageNbtProvider::id,
		StorageNbtProvider::new
	);

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.STORAGE;
	}

	@Override
	public @NotNull Tag next(Context context) {
		return ((CommandStorageHolder) context.getLevel()).neo_apoli$get(this.id());
	}

}
