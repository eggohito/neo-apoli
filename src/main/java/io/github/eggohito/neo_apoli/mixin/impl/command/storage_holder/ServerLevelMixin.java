package io.github.eggohito.neo_apoli.mixin.impl.command.storage_holder;

import io.github.eggohito.neo_apoli.impl.misc.CommandStorageHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements CommandStorageHolder {

	@Shadow
	@NotNull
	public abstract MinecraftServer getServer();

	@Override
	public CompoundTag neo_apoli$getStorage(ResourceLocation id) {
		return this.neo_apoli$getStorageHolder().neo_apoli$getStorage(id);
	}

	@Override
	public void neo_apoli$setStorage(ResourceLocation id, CompoundTag nbt) {
		this.neo_apoli$getStorageHolder().neo_apoli$setStorage(id, nbt);
	}

	@Override
	public void neo_apoli$clear() {
		this.neo_apoli$getStorageHolder().neo_apoli$clear();
	}

	@Override
	public void neo_apoli$sendAll(ServerPlayer player) {
		this.neo_apoli$getStorageHolder().neo_apoli$sendAll(player);
	}

	@Unique
	private CommandStorageHolder neo_apoli$getStorageHolder() {
		return (CommandStorageHolder) this.getServer();
	}

}
