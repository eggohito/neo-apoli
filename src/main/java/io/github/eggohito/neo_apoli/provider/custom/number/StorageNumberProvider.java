package io.github.eggohito.neo_apoli.provider.custom.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.NumberProviderTypes;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public record StorageNumberProvider(Identifier storage, NbtPathArgumentType.NbtPath path) implements NumberProvider {

	public static final MapCodec<StorageNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.fieldOf("storage").forGetter(StorageNumberProvider::storage),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(StorageNumberProvider::path)
	).apply(instance, StorageNumberProvider::new));

	public static final PacketCodec<RegistryByteBuf, StorageNumberProvider> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, StorageNumberProvider::storage,
		NeoApoliPacketCodecs.NBT_PATH, StorageNumberProvider::path,
		StorageNumberProvider::new
	);

	@Override
	public Number get(ErrorReporter reporter, ValueProviderContext context) {
		NbtCompound rootNbt = ((DataCommandStorageHolder) context.getWorld()).neo_apoli$get(this.storage());
		return this.path().count(rootNbt);
	}

	@Override
	public Type<?> getType() {
		return NumberProviderTypes.STORAGE;
	}

}
