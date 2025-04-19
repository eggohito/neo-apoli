package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin implements DataCommandStorageHolder {

	@Shadow
	@Final
	private MinecraftClient client;

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
		return (DataCommandStorageHolder) this.client;
	}

}
