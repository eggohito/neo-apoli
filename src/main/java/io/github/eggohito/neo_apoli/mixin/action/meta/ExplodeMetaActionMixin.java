package io.github.eggohito.neo_apoli.mixin.action.meta;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.action.meta.ExplodeMetaAction;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ExplosionImpl.class)
public abstract class ExplodeMetaActionMixin implements Explosion {

	@Shadow
	@Final
	private ExplosionBehavior behavior;

	@WrapOperation(method = "damageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Ljava/util/List;"))
	private List<Entity> getAllEntitiesIncludingSelf(ServerWorld serverWorld, Entity entity, Box box, Operation<List<Entity>> original) {

		if (this.behavior instanceof ExplodeMetaAction.CustomExplosionBehavior) {
			return serverWorld.getNonSpectatingEntities(Entity.class, box);
		}

		else {
			return original.call(serverWorld, entity, box);
		}

	}

}
