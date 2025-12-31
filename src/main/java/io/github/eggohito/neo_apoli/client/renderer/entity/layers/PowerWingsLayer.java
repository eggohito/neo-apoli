package io.github.eggohito.neo_apoli.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.client.mixin.accessor.WingsLayerAccessor;
import io.github.eggohito.neo_apoli.power.custom.ModifyElytraRenderPower;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.Optional;

/**
 * 	A modified version of {@link WingsLayer}, which easily allows for rendering wings from powers
 */
public class PowerWingsLayer<S extends HumanoidRenderState, M extends EntityModel<S>> extends WingsLayer<S, M> {

	private final ElytraModel adultModel;
	private final ElytraModel babyModel;
	private final EquipmentLayerRenderer equipmentRenderer;

	public PowerWingsLayer(RenderLayerParent<S, M> renderer, EntityModelSet models, EquipmentLayerRenderer equipmentRenderer) {
		super(renderer, models, equipmentRenderer);
		this.adultModel = new ElytraModel(models.bakeLayer(ModelLayers.ELYTRA));
		this.babyModel = new ElytraModel(models.bakeLayer(ModelLayers.ELYTRA_BABY));
		this.equipmentRenderer = equipmentRenderer;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, S renderState, float yRot, float xRot) {

		if (!(renderState instanceof EntityRenderCache renderCache)) {
			return;
		}

		Entity entity = renderCache.neo_apoli$getEntity();
		Prioritized.InstanceCollection<ModifyElytraRenderPower.Instance> instances = new Prioritized.InstanceCollection<>(entity, ModifyElytraRenderPower.Instance.class);

		if (instances.isEmpty()) {
			return;
		}

		ResourceLocation playerElytraTexture = WingsLayerAccessor.callGetPlayerElytraTexture(renderState);
		ElytraModel elytraModel = renderState.isBaby ? this.babyModel : adultModel;

		poseStack.pushPose();
		poseStack.translate(0.0F, 0.0F, 0.125F);

		elytraModel.setupAnim(renderState);

		for (var instance : instances) {

			Context context = instance.createHolderContext();
			ResourceKey<EquipmentAsset> assetId = instance.getAssetId();

			if (!instance.isActive(context)) {
				continue;
			}

			Context colorContext = context.forChild(".color");
			Optional<DyedItemColor> color = instance.getDyedColor(colorContext);

			ItemStack stack = Items.ELYTRA.getDefaultInstance();
			DataComponentPatch.Builder componentPatch = DataComponentPatch.builder();

			color.ifPresent(dyedColor -> componentPatch.set(DataComponents.DYED_COLOR, dyedColor));
			instance.getTrim().ifPresent(trim -> componentPatch.set(DataComponents.TRIM, trim));

			stack.applyComponentsAndValidate(componentPatch.build());

			this.equipmentRenderer.renderLayers(
				EquipmentClientInfo.LayerType.WINGS,
				assetId,
				elytraModel,
				stack,
				poseStack,
				bufferSource,
				packedLight,
				playerElytraTexture
			);

		}

		poseStack.popPose();

	}

}
