package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.gui.GuiElement;
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
public class GuiElementPower extends Power implements Prioritized<GuiElementPower> {

	public static final MapCodec<GuiElementPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(GuiElement.CODEC.fieldOf("gui_element").forGetter(GuiElementPower::getGuiElement))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(GuiElementPower::getPriority))
		.apply(instance, GuiElementPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, GuiElementPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		GuiElement.STREAM_CODEC, GuiElementPower::getGuiElement,
		ByteBufCodecs.INT, GuiElementPower::getPriority,
		GuiElementPower::new
	);

	private final GuiElement guiElement;
	private final int priority;

	public GuiElementPower(Optional<Condition> activeCondition, GuiElement guiElement, int priority) {
		super(activeCondition);
		this.guiElement = guiElement;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.GUI_ELEMENT;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends Power.Instance<GuiElementPower> {

		protected Instance(@NotNull Entity holder, @NotNull GuiElementPower power) {
			super(holder, power);
		}

		public GuiElement getGuiElement() {
			return power.getGuiElement();
		}

	}

}
