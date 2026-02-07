package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;

@EqualsAndHashCode
@Getter
public class HudRenderPower extends Power {

	public static final MapCodec<HudRenderPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
		.group(ExtraCodecs.nonEmptyList(HudElement.CODEC.listOf()).fieldOf("hud_elements").forGetter(HudRenderPower::getHudElements))
		.apply(instance, HudRenderPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HudRenderPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.collection(ObjectArrayList::new, HudElement.STREAM_CODEC), HudRenderPower::getHudElements,
		HudRenderPower::new
	);

	private final List<HudElement> hudElements;

	public HudRenderPower(List<HudElement> hudElements) {
		this.hudElements = hudElements;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.HUD_RENDER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);
		ListIterator<HudElement> listIterator = getHudElements().listIterator();

		while (listIterator.hasNext()) {

			Context.Validator hudElementValidator = validator.forChild(".hud_elements[" + listIterator.nextIndex() + "]");

			listIterator.next().validate(hudElementValidator);

		}

	}

	public static class Instance extends Power.Instance<HudRenderPower> {

		protected Instance(@NotNull Entity holder, @NotNull HudRenderPower power) {
			super(holder, power);
		}

		public List<HudElement> getHudElements() {
			return power.getHudElements();
		}

	}

}
