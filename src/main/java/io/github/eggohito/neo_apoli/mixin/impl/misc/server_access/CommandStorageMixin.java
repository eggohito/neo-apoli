package io.github.eggohito.neo_apoli.mixin.impl.misc.server_access;

import io.github.eggohito.neo_apoli.impl.misc.ServerAccess;
import io.github.eggohito.neo_apoli.network.packet.clientbound.ClientboundCommandStorageUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.CommandStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(CommandStorage.class)
public abstract class CommandStorageMixin implements ServerAccess {

	@Shadow
	public abstract CompoundTag get(ResourceLocation id);

	@Unique
	private MinecraftServer neo_apoli$server;

	@Override
	public MinecraftServer neo_apoli$getServer() {
		return Objects.requireNonNull(neo_apoli$server, "Command storage wasn't initialized properly!");
	}

	@Override
	public void neo_apoli$setServer(MinecraftServer server) {
		this.neo_apoli$server = server;
	}

	@Inject(method = "set", at = @At("TAIL"))
	private void syncNewStorageValue(ResourceLocation id, CompoundTag nbt, CallbackInfo ci) {

		for (ServerPlayer serverPlayer : this.neo_apoli$getServer().getPlayerList().getPlayers()) {
			ServerPlayNetworking.send(serverPlayer, new ClientboundCommandStorageUpdatePacket(id, nbt));
		}

	}

}
