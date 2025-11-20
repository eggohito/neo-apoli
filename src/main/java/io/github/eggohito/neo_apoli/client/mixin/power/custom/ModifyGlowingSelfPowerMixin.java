package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.power.custom.ModifyGlowingSelfPower;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class ModifyGlowingSelfPowerMixin {

	@Mixin(MinecraftClient.class)
	public static abstract class GlowingProxy {

		@Shadow public abstract @Nullable Entity getCameraEntity();

		@Unique
		private WeakReference<Context> neo_apoli$glowingContext;

		@Unique
		private Context neo_apoli$getOrCreateGlowingContext(Entity entity) {

			Context context = Optional.ofNullable(this.neo_apoli$glowingContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyGlowingSelfPower.createContext(this.getCameraEntity(), entity));

			this.neo_apoli$glowingContext = new WeakReference<>(context);
			return context;

		}

		@ModifyExpressionValue(method = "hasOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z"))
		private boolean neo_apoli$applyProxy(boolean original, Entity entity) {

			Context context = this.neo_apoli$getOrCreateGlowingContext(entity);
			boolean result = original
				|| PowersComponent.hasInstances(context.required(NeoApoliContextParameters.TARGET), ModifyGlowingSelfPower.Instance.class, instance -> instance.isActive(context));

			this.neo_apoli$glowingContext.clear();
			return result;

		}

	}

	@Environment(EnvType.CLIENT)
	@Mixin(Entity.class)
	public static abstract class CustomGlowingColor {

		@Unique
		private WeakReference<Context> neo_apoli$glowingContext;

		@Unique
		private Context neo_apoli$getOrCreateGlowingContext(Entity entity) {

			Context context = Optional.ofNullable(this.neo_apoli$glowingContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyGlowingSelfPower.createContext(MinecraftClient.getInstance().getCameraEntity(), entity));

			this.neo_apoli$glowingContext = new WeakReference<>(context);
			return context;

		}

		@Shadow
		@Nullable
		public abstract Team getScoreboardTeam();

		@ModifyReturnValue(method = "getTeamColorValue", at = @At("RETURN"))
		private int neo_apoli$modifyColor(int original) {

			Entity renderedEntity = (Entity) (Object) this;
			AbstractTeam team = this.getScoreboardTeam();

			boolean hasTeamColor = team != null
				&& team.getColor().getColorValue() != null;

			Context context = this.neo_apoli$getOrCreateGlowingContext(renderedEntity);
			int color = original;

			for (var instance: PowersComponent.getInstances(renderedEntity, ModifyGlowingSelfPower.Instance.class, instance -> instance.isActive(context) && (!hasTeamColor || !instance.shouldUseTeamColor(context)))) {
				color = Color.mix(color, instance.getColor(context));
			}

			this.neo_apoli$glowingContext.clear();
			return color;

		}

	}

}
