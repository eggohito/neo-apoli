package io.github.eggohito.neo_apoli.client.mixin.power.custom;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.power.custom.ModifyGlowingOtherPower;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

public abstract class ModifyGlowingOtherPowerMixin {

	@Mixin(Minecraft.class)
	public static abstract class GlowingProxy {

		@Shadow
		public abstract @Nullable Entity getCameraEntity();

		@ModifyExpressionValue(method = "shouldEntityAppearGlowing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z"))
		private boolean neo_apoli$applyProxy(boolean original, Entity entity) {

			try {
				return original
					|| ModifyGlowingOtherPower.modifyGlowing(this.getCameraEntity(), entity);
			}

			finally {
				ModifyGlowingOtherPower.VISITOR.clear();
			}

		}

	}

	@Environment(EnvType.CLIENT)
	@Mixin(Entity.class)
	public static abstract class CustomGlowingColor {

		@Shadow
		@Nullable
		public abstract PlayerTeam getTeam();

		@ModifyReturnValue(method = "getTeamColor", at = @At("RETURN"))
		private int neo_apoli$modifyColor(int original) {

			Entity renderedEntity = (Entity) (Object) this;
			Team team = this.getTeam();

			try {
				boolean hasTeamColor = team != null && team.getColor().getColor() != null;
				return ModifyGlowingOtherPower.modifyColor(Minecraft.getInstance().getCameraEntity(), renderedEntity, hasTeamColor, original);
			}

			finally {
				ModifyGlowingOtherPower.VISITOR.clear();
			}

		}

	}

}
