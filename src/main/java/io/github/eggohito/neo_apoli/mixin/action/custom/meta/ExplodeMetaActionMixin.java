package io.github.eggohito.neo_apoli.mixin.action.custom.meta;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.eggohito.neo_apoli.action.custom.meta.ExplodeMetaAction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.phys.AABB;

@Mixin(ServerExplosion.class)
public abstract class ExplodeMetaActionMixin implements Explosion {

	@Shadow
	@Final
	private ExplosionDamageCalculator damageCalculator;

	@WrapOperation(method = "hurtEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
	private List<Entity> getAllEntitiesIncludingSelf(ServerLevel serverWorld, Entity entity, AABB box, Operation<List<Entity>> original) {

		if (this.damageCalculator instanceof ExplodeMetaAction.DamageCalculator) {
			return serverWorld.getEntitiesOfClass(Entity.class, box);
		}

		else {
			return original.call(serverWorld, entity, box);
		}

	}

}
