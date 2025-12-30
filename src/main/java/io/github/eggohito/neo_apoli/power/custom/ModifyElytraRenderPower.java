package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.client.renderer.entity.layers.PowerWingsLayer;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyElytraRenderPower extends Power implements Prioritized<ModifyElytraRenderPower> {

	public static final MapCodec<ModifyElytraRenderPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ResourceKey.codec(EquipmentAssets.ROOT_ID).fieldOf("asset_id").forGetter(ModifyElytraRenderPower::getAssetId))
		.and(ArmorTrim.CODEC.optionalFieldOf("trim").forGetter(ModifyElytraRenderPower::getTrim))
		.and(Color.CODEC.optionalFieldOf("color").forGetter(ModifyElytraRenderPower::getColor))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyElytraRenderPower::getPriority))
		.apply(instance, ModifyElytraRenderPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyElytraRenderPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		ResourceKey.streamCodec(EquipmentAssets.ROOT_ID), ModifyElytraRenderPower::getAssetId,
		ByteBufCodecs.optional(ArmorTrim.STREAM_CODEC), ModifyElytraRenderPower::getTrim,
		ByteBufCodecs.optional(Color.STREAM_CODEC), ModifyElytraRenderPower::getColor,
		ByteBufCodecs.INT, ModifyElytraRenderPower::getPriority,
		ModifyElytraRenderPower::new
	);

	private final ResourceKey<EquipmentAsset> assetId;
	private final Optional<ArmorTrim> trim;
	private final Optional<Color> color;
	private final int priority;

	public ModifyElytraRenderPower(Optional<Condition> activeCondition, ResourceKey<EquipmentAsset> assetId, Optional<ArmorTrim> trim, Optional<Color> color, int priority) {
		super(activeCondition);
		this.assetId = assetId;
		this.trim = trim;
		this.color = color;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ELYTRA_RENDER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<ModifyElytraRenderPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyElytraRenderPower power) {
			super(holder, power);
		}

		public ResourceKey<EquipmentAsset> getAssetId() {
			return power.getAssetId();
		}

		@Nullable
		public ArmorTrim getNullableTrim() {
			return power.getTrim().orElse(null);
		}

		public int getColor(Context context) {
			return power.getColor()
				.map(innerColor -> innerColor.getValue(context.forChild(".color")))
				.orElse(0);
		}

	}

	@Environment(EnvType.CLIENT)
	public static void prepareRenderLayer(EntityType<? extends LivingEntity> entityType, LivingEntityRenderer<?, ?, ?> renderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererProvider.Context context) {

		switch (renderer) {
			case HumanoidMobRenderer<?, ?, ?> humanoidMobRenderer ->
				registrationHelper.register(new PowerWingsLayer<>(humanoidMobRenderer, context.getModelSet(), context.getEquipmentRenderer()));
			case PlayerRenderer playerRenderer ->
				registrationHelper.register(new PowerWingsLayer<>(playerRenderer, context.getModelSet(), context.getEquipmentRenderer()));
			case ArmorStandRenderer armorStandRenderer ->
				registrationHelper.register(new PowerWingsLayer<>(armorStandRenderer, context.getModelSet(), context.getEquipmentRenderer()));
			default -> {
				//	No-op because it's unsupported
			}
		}

	}

}
