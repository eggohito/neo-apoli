package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class HudRenderPower extends Power implements Prioritized<HudRenderPower> {

	public static final MapCodec<HudRenderPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(HudElement.CODEC.fieldOf("hud_element").forGetter(HudRenderPower::getHudElement))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(HudRenderPower::getPriority))
		.apply(instance, HudRenderPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HudRenderPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		HudElement.STREAM_CODEC, HudRenderPower::getHudElement,
		ByteBufCodecs.INT, HudRenderPower::getPriority,
		HudRenderPower::new
	);

	private final HudElement hudElement;
	private final int priority;

	public HudRenderPower(Optional<Condition> activeCondition, HudElement hudElement, int priority) {
		super(activeCondition);
		this.hudElement = hudElement;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.HUD_RENDER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<HudRenderPower> {

		protected Instance(@NotNull Entity holder, @NotNull HudRenderPower power) {
			super(holder, power);
		}

		public HudElement getHudElement() {
			return power.getHudElement();
		}

	}

}
