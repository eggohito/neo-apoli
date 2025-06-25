package io.github.eggohito.neo_apoli.provider.custom.string;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderType;
import io.github.eggohito.neo_apoli.provider.type.string.StringProviderTypes;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public final class StorageStringProvider extends StringProvider {

	public static final MapCodec<StorageStringProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Identifier.CODEC.fieldOf("storage").forGetter(StorageStringProvider::storage),
		NbtPathArgumentType.NbtPath.CODEC.fieldOf("path").forGetter(StorageStringProvider::path)
	).apply(instance, StorageStringProvider::new));

	public static final PacketCodec<RegistryByteBuf, StorageStringProvider> PACKET_CODEC = PacketCodec.tuple(
		Identifier.PACKET_CODEC, StorageStringProvider::storage,
		NeoApoliPacketCodecs.NBT_PATH, StorageStringProvider::path,
		StorageStringProvider::new
	);

	private final Identifier storage;
	private final NbtPathArgumentType.NbtPath path;

	public StorageStringProvider(Identifier storage, NbtPathArgumentType.NbtPath path) {
		this.storage = storage;
		this.path = path;
	}

	@Override
	public StringProviderType<?> getType() {
		return StringProviderTypes.STORAGE;
	}

	@Override
	protected String stringImpl(Context context) {

		try {

			NbtCompound rootNbt = ((DataCommandStorageHolder) context.getWorld()).neo_apoli$get(this.storage());
			List<NbtElement> elements = this.path().get(rootNbt);

			if (elements.size() > 1) {
				return Integer.toString(this.path().count(rootNbt));
			}

			else if (elements.size() == 1) {
				NbtElement element = elements.getFirst();
				return element.asString().orElseGet(element::toString);
			}

			else {
				return "";
			}

		}

		catch (CommandSyntaxException cse) {
			context.getReporter().report("Error trying to get string in NBT path \"" + this.path() + "\" from storage \"" + this.storage() + "\": " + cse.getMessage());
			return "";
		}

	}

}
