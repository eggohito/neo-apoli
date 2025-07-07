package io.github.eggohito.neo_apoli.provider.custom.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.NbtProvider;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderType;
import io.github.eggohito.neo_apoli.provider.type.nbt.NbtProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode
@Data
public final class StorageNbtProvider extends NbtProvider {

	public static final MapCodec<StorageNbtProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.fieldOf("id").forGetter(StorageNbtProvider::id)
	).apply(instance, StorageNbtProvider::new));

	public static final PacketCodec<RegistryByteBuf, StorageNbtProvider> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, StorageNbtProvider::id,
		StorageNbtProvider::new
	);

	private final Identifier id;

	@Override
	public NbtProviderType<?> getType() {
		return NbtProviderTypes.STORAGE;
	}

	@Override
	protected NbtElement impl(Context context) {
		return ((DataCommandStorageHolder) context.getWorld()).neo_apoli$get(this.id());
	}

}
