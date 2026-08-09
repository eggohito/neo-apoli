package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.modify_model_color_self;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.eggohito.neo_apoli.impl.misc.EntityCache;
import io.github.eggohito.neo_apoli.power.custom.ModifyModelColorSelfPower;
import io.github.eggohito.neo_apoli.power.entity.Powers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.lang.ref.WeakReference;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin implements EntityCache {

	@Unique
	protected WeakReference<Entity> neo_apoli$entity = null;

	@Nullable
	@Override
	public Entity neo_apoli$getEntity() {

		if (neo_apoli$entity != null) {
			return neo_apoli$entity.get();
		}

		else {
			return null;
		}

	}

	@Override
	public void neo_apoli$setEntity(@Nullable Entity entity) {
		this.neo_apoli$entity = entity == null ? null : new WeakReference<>(entity);
	}

	@WrapOperation(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
	void modifyArmModelColor(ModelPart armPart, PoseStack matrices, VertexConsumer vertices, int light, int overlay, Operation<Void> original) {

		Entity entity = this.neo_apoli$getEntity();

		if (entity != null && Powers.hasInstances(entity, ModifyModelColorSelfPower.Instance.class)) {
			armPart.render(matrices, vertices, light, overlay, ModifyModelColorSelfPower.modify(null, entity, -1));
		}

		else {
			original.call(armPart, matrices, vertices, light, overlay);
		}

	}

}
