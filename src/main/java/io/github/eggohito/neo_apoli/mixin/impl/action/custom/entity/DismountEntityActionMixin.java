package io.github.eggohito.neo_apoli.mixin.impl.action.custom.entity;

import io.github.eggohito.neo_apoli.network.packet.s2c.DismountEntityS2CPacket;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class DismountEntityActionMixin {

	@Shadow
	public abstract @Nullable Entity getVehicle();

	@Shadow
	public abstract int getId();

	@Inject(method = "removeVehicle", at = @At("HEAD"))
	private void syncDismount(CallbackInfo ci) {

		if (this.getVehicle() instanceof ServerPlayer) {
			MiscUtil.sendToTracking((Entity) (Object) this, new DismountEntityS2CPacket(this.getId()));
		}

	}

}
