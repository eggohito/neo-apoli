package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.context.Context;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EqualsAndHashCode
@Getter
public class HudRenderPower extends Power {

	public static final MapCodec<HudRenderPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
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

		ListIterator<HudElement> iterator = this.getHudElements().listIterator();

		while (iterator.hasNext()) {

			int index = iterator.nextIndex();
			HudElement hudElement = iterator.next();

			hudElement.validate(validator.forChild(".hud_elements[" + index + "]"));

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

	@Environment(EnvType.CLIENT)
	public static void prepareHudElements(Consumer<Consumer<Power.Instance<?>>> prepare, HudRenderPhase renderPhase, BiConsumer<Context, HudElement> adder) {

		boolean hideGui = Minecraft.getInstance().options.hideGui;
		Consumer<Power.Instance<?>> preparer = instance -> {

			if (!(instance instanceof Instance hudRenderInstance)) {
				return;
			}

			Context context = instance.createHolderContext();
			ListIterator<HudElement> listIterator = hudRenderInstance.getHudElements().listIterator();

			while (listIterator.hasNext()) {

				int index = listIterator.nextIndex();
				HudElement hudElement = listIterator.next();

				Context hudContext = context.forChild(".hud_elements[" + index + "]");
				boolean doNotHide = !hideGui || !hudElement.hideWithHud(hudContext);

				if (doNotHide && hudElement.shouldRender(hudContext, renderPhase)) {
					adder.accept(hudContext, hudElement);
				}

			}

		};

		prepare.accept(preparer);

	}

}
