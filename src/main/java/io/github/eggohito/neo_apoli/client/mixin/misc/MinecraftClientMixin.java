package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements DataCommandStorageHolder {

	@Unique
	private final Object2ObjectOpenHashMap<Identifier, NbtCompound> neo_apoli$commandStorageCache = new Object2ObjectOpenHashMap<>();

	@Override
	public NbtCompound neo_apoli$get(Identifier id) {
		return neo_apoli$commandStorageCache.getOrDefault(id, new NbtCompound());
	}

	@Override
	public void neo_apoli$set(Identifier id, NbtCompound nbt) {

		if (nbt.isEmpty()) {
			neo_apoli$commandStorageCache.remove(id);
		}

		else {
			neo_apoli$commandStorageCache.put(id, nbt);
		}

	}

	@Override
	public void neo_apoli$clear() {
		this.neo_apoli$commandStorageCache.clear();
		this.neo_apoli$commandStorageCache.trim();
	}

}
