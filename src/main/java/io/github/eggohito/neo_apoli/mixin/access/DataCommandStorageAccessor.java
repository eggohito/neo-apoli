package io.github.eggohito.neo_apoli.mixin.access;

import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DataCommandStorage.class)
public interface DataCommandStorageAccessor {

	@Accessor
	Map<String, DataCommandStorage.PersistentState> getStorages();

	@Mixin(DataCommandStorage.PersistentState.class)
	interface PersistentStateAccessor {

		@Accessor("map")
		Map<String, NbtCompound> getData();

	}

}
