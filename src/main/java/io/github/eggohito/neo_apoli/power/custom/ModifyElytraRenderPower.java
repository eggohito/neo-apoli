package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;

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

		public Optional<ArmorTrim> getTrim() {
			return power.getTrim();
		}

		public Optional<DyedItemColor> getDyedColor(Context context) {
			return power.getColor()
				.map(color -> color.getValue(context.forChild(".color")))
				.map(DyedItemColor::new);
		}

	}

}
