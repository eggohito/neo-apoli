package io.github.eggohito.neo_apoli.duck;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

public interface DataCommandStorageHolder {

	NbtCompound neo_apoli$get(Identifier id);

	void neo_apoli$set(Identifier id, NbtCompound nbt);

	@ApiStatus.Internal
	default void neo_apoli$clear() {

	}

	@ApiStatus.Internal
	default void neo_apoli$sendAll(ServerPlayerEntity player) {

	}

}
