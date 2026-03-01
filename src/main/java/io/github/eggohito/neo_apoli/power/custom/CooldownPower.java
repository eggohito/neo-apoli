package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.hud.HudElement;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.HudRenderPhase;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class CooldownPower extends Power {

	public static final MapCodec<CooldownPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		HudElement.CODEC.fieldOf("hud_element").forGetter(CooldownPower::getHudElement),
		NumberProvider.CODEC.fieldOf("cooldown").forGetter(CooldownPower::getCooldown)
	).apply(instance, CooldownPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, CooldownPower> STREAM_CODEC = StreamCodec.composite(
		HudElement.STREAM_CODEC, CooldownPower::getHudElement,
		NumberProvider.STREAM_CODEC, CooldownPower::getCooldown,
		CooldownPower::new
	);

	private final HudElement hudElement;
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
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getHudElement().validate(validator.forChild(".hud_element"));
		getCooldown().validate(validator.forChild(".cooldown"));
	}

	public static class Instance extends Power.Instance<CooldownPower> {

		protected static final MapCodec<Long> LAST_USE_TIME_CODEC = Codec.LONG.fieldOf("last_use_time");
		protected long lastUseTime;

		protected Instance(@NotNull Entity holder, @NotNull CooldownPower power) {
			super(holder, power);
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, MapLike<I> mapInput) {
			return LAST_USE_TIME_CODEC.decode(ops, mapInput)
				.map(value -> this.lastUseTime = value)
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(RegistryOps<O> ops, RecordBuilder<O> prefix) {
			return LAST_USE_TIME_CODEC.encode(this.lastUseTime, ops, prefix);
		}

		public HudElement getHudElement() {
			return power.getHudElement();
		}

		public NumberProvider getCooldown() {
			return power.getCooldown();
		}

		public Context createContext() {

			Context holderContext = this.createHolderContext();

			return new Context.Builder(holderContext)
				.withRequired(NeoApoliContextParams.CURRENT_VALUE, (double) this.getRemainingTicks(holderContext))
				.withRequired(NeoApoliContextParams.MIN_VALUE, 0.0D)
				.withRequired(NeoApoliContextParams.MAX_VALUE, power.getCooldown().nextDouble(holderContext))
				.build(holderContext.level());

		}

		public boolean shouldRender(Context context, HudRenderPhase renderPhase) {

			long timePassed = holder.level().getGameTime() - lastUseTime;
			int cooldown = power.getCooldown().nextInt(context.forChild(".cooldown"));

			return timePassed <= cooldown
				&& getHudElement().shouldRender(context, renderPhase);

		}

		public double getProgress(Context context) {

			double diff = holder.level().getGameTime() - lastUseTime;
			double progress = diff / getCooldown().nextDouble(context.forChild(".cooldown"));

			return Mth.clamp(progress, 0D, 1D);

		}

		public int getRemainingTicks(Context context) {

			long diff = holder.level().getGameTime() - lastUseTime;
			long remainingTicks = getCooldown().nextLong(context.forChild(".cooldown")) - diff;

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

}
