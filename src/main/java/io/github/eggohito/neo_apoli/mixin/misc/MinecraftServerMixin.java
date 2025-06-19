package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.duck.DataCommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.MinecraftServerAccess;
import io.github.eggohito.neo_apoli.mixin.access.DataCommandStorageAccessor;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizeDataCommandStorageS2CPacket;
import io.github.eggohito.neo_apoli.power.PowerManager;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements DataCommandStorageHolder {

	@Shadow
	public abstract DataCommandStorage getDataCommandStorage();

	@ModifyExpressionValue(method = "createWorlds", at = @At(value = "NEW", target = "(Lnet/minecraft/world/PersistentStateManager;)Lnet/minecraft/command/DataCommandStorage;"))
	private DataCommandStorage cacheServerInDataCommandStorage(DataCommandStorage original) {
		((MinecraftServerAccess) original).neo_apoli$setServer((MinecraftServer) (Object) this);
		return original;
	}

	@Override
	public NbtCompound neo_apoli$get(Identifier id) {
		return this.getDataCommandStorage().get(id);
	}

	@Override
	public void neo_apoli$set(Identifier id, NbtCompound nbt) {
		this.getDataCommandStorage().set(id, nbt);
	}

	@Override
	public void neo_apoli$sendAll(ServerPlayerEntity player) {

		Map<String, DataCommandStorage.PersistentState> storages = ((DataCommandStorageAccessor) this.getDataCommandStorage()).getStorages();

		for (var storage : storages.entrySet()) {

			String namespace = storage.getKey();
			DataCommandStorage.PersistentState persistentState = storage.getValue();

			Map<String, NbtCompound> data = ((DataCommandStorageAccessor.PersistentStateAccessor) persistentState).getData();

			for (var entry : data.entrySet()) {

				String path = entry.getKey();
				NbtCompound nbt = entry.getValue();

				ServerPlayNetworking.send(player, new SynchronizeDataCommandStorageS2CPacket(Identifier.of(namespace, path), nbt));

			}

		}

	}

	@Inject(method = "method_29440", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/DataPackContents;applyPendingTagLoads()V"))
	private void onDataPacksReloaded(Collection<String> dataPacks, MinecraftServer.ResourceManagerHolder resourceManagerHolder, CallbackInfo ci) {
		PowerManager.validate(resourceManagerHolder.dataPackContents());
	}

}
