package io.github.eggohito.neo_apoli.mixin.misc;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.duck.CommandStorageHolder;
import io.github.eggohito.neo_apoli.duck.MinecraftServerAccess;
import io.github.eggohito.neo_apoli.mixin.access.CommandStorageAccessor;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizeCommandStorageS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.CommandStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements CommandStorageHolder {

	@Shadow
	public abstract CommandStorage getCommandStorage();

	@ModifyExpressionValue(method = "createLevels", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/storage/DimensionDataStorage;)Lnet/minecraft/world/level/storage/CommandStorage;"))
	private CommandStorage cacheServerToCommandStorage(CommandStorage original) {
		((MinecraftServerAccess) original).neo_apoli$setServer((MinecraftServer) (Object) this);
		return original;
	}

	@Override
	public CompoundTag neo_apoli$get(ResourceLocation id) {
		return this.getCommandStorage().get(id);
	}

	@Override
	public void neo_apoli$set(ResourceLocation id, CompoundTag nbt) {
		this.getCommandStorage().set(id, nbt);
	}

	@Override
	public void neo_apoli$sendAll(ServerPlayer player) {

		Map<String, CommandStorage.Container> storages = ((CommandStorageAccessor) this.getCommandStorage()).getNamespaces();

		for (var storage : storages.entrySet()) {

			String namespace = storage.getKey();
			CommandStorage.Container persistentState = storage.getValue();

			Map<String, CompoundTag> data = ((CommandStorageAccessor.ContainerAccessor) persistentState).getData();

			for (var entry : data.entrySet()) {

				String path = entry.getKey();
				CompoundTag nbt = entry.getValue();

				ServerPlayNetworking.send(player, new SynchronizeCommandStorageS2CPacket(ResourceLocation.fromNamespaceAndPath(namespace, path), nbt));

			}

		}

	}

}
