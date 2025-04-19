package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.duck.ServerAccess;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeDataCommandStorageS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(DataCommandStorage.class)
public abstract class DataCommandStorageMixin implements ServerAccess {

	@Shadow
	public abstract NbtCompound get(Identifier id);

	@Unique
	private MinecraftServer neo_apoli$server;

	@Override
	public MinecraftServer neo_apoli$getServer() {
		return Objects.requireNonNull(neo_apoli$server, "Data command storage wasn't initialized properly!");
	}

	@Override
	public void neo_apoli$setServer(MinecraftServer server) {
		this.neo_apoli$server = server;
	}

	@Inject(method = "set", at = @At("HEAD"))
	private void cacheOldStorageValue(Identifier id, NbtCompound nbt, CallbackInfo ci, @Share("oldNbt") LocalRef<NbtCompound> oldNbt) {
		oldNbt.set(this.get(id));
	}

	@Inject(method = "set", at = @At("TAIL"))
	private void syncNewStorageValue(Identifier id, NbtCompound nbt, CallbackInfo ci, @Share("oldNbt") LocalRef<NbtCompound> oldNbt) {

		for (ServerPlayerEntity player : this.neo_apoli$getServer().getPlayerManager().getPlayerList()) {
			ServerPlayNetworking.send(player, new SynchronizeDataCommandStorageS2CPacket(id, nbt));
		}

	}

}
