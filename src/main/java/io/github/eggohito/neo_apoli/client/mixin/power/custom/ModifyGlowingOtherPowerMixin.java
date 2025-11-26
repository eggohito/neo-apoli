package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyGlowingOtherPower;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;
import java.util.Optional;

public abstract class ModifyGlowingOtherPowerMixin {

	@Mixin(Minecraft.class)
	public static abstract class GlowingProxy {

		@Shadow
		public abstract @Nullable Entity getCameraEntity();

		@Unique
		private WeakReference<Context> neo_apoli$glowingContext;

		@Unique
		private Context neo_apoli$getOrCreateGlowingContext(Entity entity) {

			Context context = Optional.ofNullable(this.neo_apoli$glowingContext)
				.flatMap(reference -> Optional.ofNullable(reference.get()))
				.orElseGet(() -> ModifyGlowingOtherPower.createContext(this.getCameraEntity(), entity));

			this.neo_apoli$glowingContext = new WeakReference<>(context);
			return context;

		}

		@ModifyExpressionValue(method = "shouldEntityAppearGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z"))
		private boolean neo_apoli$applyProxy(boolean original, Entity entity) {

			Context context = this.neo_apoli$getOrCreateGlowingContext(entity);
			boolean result = original || ModifyGlowingOtherPower.modifyOutlineVisibility(context);

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
				.orElseGet(() -> ModifyGlowingOtherPower.createContext(Minecraft.getInstance().getCameraEntity(), entity));

			this.neo_apoli$glowingContext = new WeakReference<>(context);
			return context;

		}

		@Shadow
		@Nullable
		public abstract PlayerTeam getTeam();

		@ModifyReturnValue(method = "getTeamColor", at = @At("RETURN"))
		private int neo_apoli$modifyColor(int original) {

			Entity renderedEntity = (Entity) (Object) this;
			Team team = this.getTeam();

			boolean hasTeamColor = team != null
				&& team.getColor().getColor() != null;

			Context context = this.neo_apoli$getOrCreateGlowingContext(renderedEntity);
			int color = ModifyGlowingOtherPower.modifyColor(context, hasTeamColor, original);

			this.neo_apoli$glowingContext.clear();
			return color;

		}

	}

}
