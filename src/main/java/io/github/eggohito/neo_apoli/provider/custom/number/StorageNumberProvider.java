package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderType;
import io.github.eggohito.neo_apoli.provider.type.number.NumberProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

@EqualsAndHashCode
@Data
public final class StorageNumberProvider extends NumberProvider {

	public static final MapCodec<StorageNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.fieldOf("storage").forGetter(StorageNumberProvider::storage),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(StorageNumberProvider::path)
	).apply(instance, StorageNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, StorageNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, StorageNumberProvider::storage,
		NeoApoliPacketCodecs.NBT_PATH, StorageNumberProvider::path,
		StorageNumberProvider::new
	);

	private final Identifier storage;
	private final NbtPathArgumentType.NbtPath path;

	public StorageNumberProvider(Identifier storage, NbtPathArgumentType.NbtPath path) {
		this.storage = storage;
		this.path = path;
	}

	@Override
	public NumberProviderType<?> getType() {
		return NumberProviderTypes.STORAGE;
	}

	@Override
	protected Number impl(Context context) {
		return this.path().count(((DataCommandStorageHolder) context.getWorld()).neo_apoli$get(this.storage()));
	}

}
