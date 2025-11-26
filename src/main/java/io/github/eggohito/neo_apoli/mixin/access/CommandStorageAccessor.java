package io.github.eggohito.neo_apoli.mixin.access;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.CommandStorage;

@Mixin(CommandStorage.class)
public interface CommandStorageAccessor {

	@Accessor
	Map<String, CommandStorage.Container> getNamespaces();

	@Mixin(CommandStorage.Container.class)
	interface ContainerAccessor {

		@Accessor("storage")
		Map<String, CompoundTag> getData();

	}

}
