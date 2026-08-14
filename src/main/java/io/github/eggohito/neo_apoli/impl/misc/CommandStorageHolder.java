package io.github.eggohito.neo_apoli.impl.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

public interface CommandStorageHolder {

	CompoundTag neo_apoli$getStorage(ResourceLocation id);

	boolean neo_apoli$contains(ResourceLocation id);

	void neo_apoli$setStorage(ResourceLocation id, CompoundTag nbt);

	@ApiStatus.Internal
	default void neo_apoli$clear() {

	}

	@ApiStatus.Internal
	default void neo_apoli$sendAll(ServerPlayer player) {

	}

}
