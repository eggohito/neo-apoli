package io.github.eggohito.neo_apoli.mixin.misc;

import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements DataCommandStorageHolder {

	@Shadow
	@NotNull
	public abstract MinecraftServer getServer();

	@Override
	public NbtCompound neo_apoli$get(Identifier id) {
		return this.neo_apoli$getStorageHolder().neo_apoli$get(id);
	}

	@Override
	public void neo_apoli$set(Identifier id, NbtCompound nbt) {
		this.neo_apoli$getStorageHolder().neo_apoli$set(id, nbt);
	}

	@Override
	public void neo_apoli$clear() {
		this.neo_apoli$getStorageHolder().neo_apoli$clear();
	}

	@Override
	public void neo_apoli$sendAll(ServerPlayerEntity player) {
		this.neo_apoli$getStorageHolder().neo_apoli$sendAll(player);
	}

	@Unique
	private DataCommandStorageHolder neo_apoli$getStorageHolder() {
		return (DataCommandStorageHolder) this.getServer();
	}

}
