package io.github.eggohito.neo_apoli.mixin.impl.misc.rideable_players;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract @Nullable Entity getVehicle();

	@Shadow
	public abstract Entity getRootVehicle();

	@ModifyExpressionValue(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;canSerialize()Z"))
    boolean allowPlayersToBeRidden(boolean original, Entity vehicle) {
        return original
            || vehicle instanceof Player;
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At("RETURN"))
    void syncMountAndSetAttachment(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> cir) {

        Entity thisAsEntity = (Entity) (Object) this;
        boolean succeeded = cir.getReturnValueZ();

	    if (succeeded) {

		    if (thisAsEntity instanceof ServerPlayer passenger) {
				passenger.setAttached(NeoApoliEntityAttachments.IS_RIDING_PLAYER, vehicle instanceof Player || this.getRootVehicle() instanceof Player);
		    }

			MiscUtil.broadcastToAll(thisAsEntity, () -> new ClientboundSetPassengersPacket(vehicle));

	    }

    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    void cacheVehicleBeforeDismount(CallbackInfo ci, @Share("vehicle") LocalRef<Entity> vehicleRef) {
        vehicleRef.set(this.getVehicle());
    }

    @Inject(method = "stopRiding", at = @At("TAIL"))
    void syncDismountAndRemoveAttachment(CallbackInfo ci, @Share("vehicle") LocalRef<Entity> vehicleRef) {

        Entity thisAsEntity = (Entity) (Object) this;
        Entity vehicle = vehicleRef.get();

        thisAsEntity.removeAttached(NeoApoliEntityAttachments.IS_RIDING_PLAYER);

        if (vehicle != null) {
            MiscUtil.broadcastToAll(thisAsEntity, () -> new ClientboundSetPassengersPacket(vehicle));
        }

    }

}
