package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements CommandStorageHolder {

	@Unique
	private final Object2ObjectOpenHashMap<ResourceLocation, CompoundTag> neo_apoli$commandStorageCache = new Object2ObjectOpenHashMap<>();

	@Override
	public CompoundTag neo_apoli$getStorage(ResourceLocation id) {
		return neo_apoli$commandStorageCache.getOrDefault(id, new CompoundTag());
	}

	@Override
	public void neo_apoli$setStorage(ResourceLocation id, CompoundTag nbt) {

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
