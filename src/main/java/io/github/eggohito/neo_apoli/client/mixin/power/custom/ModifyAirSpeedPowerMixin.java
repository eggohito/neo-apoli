package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyAirSpeedPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

@Mixin(LocalPlayer.class)
public abstract class ModifyAirSpeedPowerMixin extends AbstractClientPlayer {

	private ModifyAirSpeedPowerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
		super(clientLevel, gameProfile);
	}

	@Unique
	protected WeakReference<Context> neo_apoli$airSpeedContext = new WeakReference<>(null);

	@Unique
	protected Context neo_apoli$getOrCreateAirSpeedContext() {

		Context context = Optional.ofNullable(this.neo_apoli$airSpeedContext)
			.flatMap(reference -> Optional.ofNullable(reference.get()))
			.orElseGet(() -> ModifyAirSpeedPower.createContext(this));

		this.neo_apoli$airSpeedContext = new WeakReference<>(context);
		return context;

	}

	@ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;getFlyingSpeed()F"))
	private float modify(float original) {

		Context context = this.neo_apoli$getOrCreateAirSpeedContext();
		List<ModifyAirSpeedPower.Instance> instances = PowersComponent.getInstances(this, ModifyAirSpeedPower.Instance.class);

		this.neo_apoli$airSpeedContext.clear();
		return ModifyAirSpeedPower.modify(context, instances, original);

	}

}
