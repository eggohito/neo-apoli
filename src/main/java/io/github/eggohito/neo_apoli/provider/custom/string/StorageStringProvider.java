package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.context.ValueProviderContext;
import io.github.eggohito.neo_apoli.provider.type.StringProviderTypes;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.List;

public record StorageStringProvider(Identifier storage, NbtPathArgumentType.NbtPath path) implements StringProvider {

	public static final MapCodec<StorageStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.fieldOf("storage").forGetter(StorageStringProvider::storage),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(StorageStringProvider::path)
	).apply(instance, StorageStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, StorageStringProvider> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, StorageStringProvider::storage,
		NeoApoliPacketCodecs.NBT_PATH, StorageStringProvider::path,
		StorageStringProvider::new
	);

	@Override
	public String get(ErrorReporter reporter, ValueProviderContext context) {

		try {

			NbtCompound rootNbt = ((DataCommandStorageHolder) context.getWorld()).neo_apoli$get(this.storage());
			List<NbtElement> elements = this.path().get(rootNbt);

			if (elements.size() == 1) {
				return elements.getFirst().toString();
			}

			else {
				return "";
			}

		}

		catch (CommandSyntaxException cse) {
			reporter.report("Error trying to get string from NBT \"" + this.path() + "\": " + cse);
			return "";
		}

	}

	@Override
	public Type<?> getType() {
		return StringProviderTypes.STORAGE;
	}

}
