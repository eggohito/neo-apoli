package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.hud.NumberBoundHudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class CooldownPower extends Power {

	public static final MapCodec<CooldownPower> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		NumberBoundHudElement.CODEC.fieldOf("hud_element").forGetter(CooldownPower::getHudElement),
		NumberProvider.CODEC.fieldOf("cooldown").forGetter(CooldownPower::getCooldown)
	).apply(instance, CooldownPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CooldownPower> STREAM_CODEC = StreamCodec.composite(
		NumberBoundHudElement.STREAM_CODEC, CooldownPower::getHudElement,
		NumberProvider.STREAM_CODEC, CooldownPower::getCooldown,
		CooldownPower::new
	);

	private final NumberBoundHudElement hudElement;
	private final NumberProvider cooldown;

	@Override
	public PowerType<?> getType() {
		return PowerTypes.COOLDOWN;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		super.validate(reporter);
		getHudElement().validate(reporter.forChild(".hud_element"));
		getCooldown().validate(reporter.forChild(".cooldown"));
	}

	public static DataResult<CooldownPower> getAsResult(PowerReference reference) {
		return PowerManager.getAsResult(reference).flatMap(
			power -> power instanceof CooldownPower cooldownPower
				? DataResult.success(cooldownPower)
				: DataResult.error(() -> reference.asDisplayString() + " does not have a cooldown!")
		);
	}

	public static class Instance extends Power.Instance<CooldownPower> {

		protected long lastUseTime;

		protected Instance(@NotNull Entity holder, @NotNull CooldownPower power) {
			super(holder, power);
		}

		@Override
		public ContextImpl.Builder createHolderContextBuilder() {

			ContextImpl.Builder builder = super.createHolderContextBuilder();
			Context context = builder.build(holder.level());

			return builder
				.add(NeoApoliContextKeys.MIN_VALUE, 0.0D)
				.add(NeoApoliContextKeys.MAX_VALUE, power.getCooldown().nextDouble(context.makeChild(".cooldown")))
				.add(NeoApoliContextKeys.CURRENT_VALUE, (double) this.getRemainingTicks(context));

		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return ops.getNumberValue(data)
				.ifSuccess(number -> this.lastUseTime = number.longValue())
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.createLong(lastUseTime));
		}

		public HudElement getHudElement() {
			return power.getHudElement();
		}

		public NumberProvider getCooldown() {
			return power.getCooldown();
		}

		public boolean shouldRender(Context context, HudRenderPhase renderPhase) {

			long timePassed = holder.level().getGameTime() - lastUseTime;
			int cooldown = power.getCooldown().nextInt(context.makeChild(".cooldown"));

			return timePassed <= cooldown
				&& getHudElement().shouldRender(context, renderPhase);

		}

		public double getProgress(Context context) {

			double diff = holder.level().getGameTime() - lastUseTime;
			double progress = diff / getCooldown().nextDouble(context.makeChild(".cooldown"));

			return Mth.clamp(progress, 0D, 1D);

		}

		public int getRemainingTicks(Context context) {

			long diff = holder.level().getGameTime() - lastUseTime;
			long remainingTicks = getCooldown().nextLong(context.makeChild(".cooldown")) - diff;

			return (int) Math.max(0, remainingTicks);

		}

		public void trigger() {

			if (holder.level().isClientSide()) {
				return;
			}

			this.lastUseTime = holder.level().getGameTime();
			this.syncData();

		}

	}

	@Environment(EnvType.CLIENT)
	public static void prepareHudElements(Consumer<Consumer<Power.Instance<?>>> prepare, HudRenderPhase renderPhase, BiConsumer<Context, HudElement> adder) {

		boolean hideGui = Minecraft.getInstance().options.hideGui;
		Consumer<Power.Instance<?>> preparer = instance -> {

			if (!(instance instanceof Instance cooldownInstance)) {
				return;
			}

			Context hudContext = cooldownInstance.createHolderContext().makeChild(".hud_element");
			HudElement hudElement = cooldownInstance.getHudElement();

			boolean doNotHide = !hideGui || !hudElement.hideWithHud(hudContext);

			if (doNotHide && cooldownInstance.shouldRender(hudContext, renderPhase)) {
				adder.accept(hudContext, hudElement);
			}

		};

		prepare.accept(preparer);

	}

}
