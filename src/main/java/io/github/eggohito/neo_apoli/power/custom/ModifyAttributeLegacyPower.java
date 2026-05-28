package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.AttributeModifyingPower;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.util.AttributedModifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyAttributeLegacyPower extends AttributeModifyingPower {

	public static final MapCodec<ModifyAttributeLegacyPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addConditionalAttributeModifyingAndFields(instance)
		.and(NumberProvider.clamped(1, Integer.MAX_VALUE).optionalFieldOf("tick_rate", new ConstantNumberProvider(20)).forGetter(ModifyAttributeLegacyPower::getTickRate))
		.apply(instance, ModifyAttributeLegacyPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyAttributeLegacyPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		NeoApoliStreamCodecs.ATTRIBUTE_MODIFIERS, AttributeModifyingPower::getModifiers,
		BooleanProvider.STREAM_CODEC, AttributeModifyingPower::getSendUpdate,
		NumberProvider.STREAM_CODEC, ModifyAttributeLegacyPower::getTickRate,
		ModifyAttributeLegacyPower::new
	);

	private final NumberProvider tickRate;

	public ModifyAttributeLegacyPower(Optional<Condition> activeCondition, List<AttributedModifier> modifiers, BooleanProvider sendUpdate, NumberProvider tickRate) {
		super(activeCondition, modifiers, sendUpdate);
		this.tickRate = tickRate;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_ATTRIBUTE_LEGACY;
	}

	@Override
	public AttributeModifyingPower.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends AttributeModifyingPower.Instance<ModifyAttributeLegacyPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Instance(@NotNull ModifyAttributeLegacyPower power) {
			super(power);
		}

		@Override
		public void onGranted(Entity holder) {

			super.onGranted(holder);

			if (!this.shouldTick(holder)) {
				this.addModifiersPersistently(holder, this.createHolderContext(holder));
			}

		}

		@Override
		public void onRespawned(Entity holder) {

			super.onRespawned(holder);

			if (!this.shouldTick(holder)) {
				this.addModifiersPersistently(holder, this.createHolderContext(holder));
			}

		}

		@Override
		public void onRevoked(Entity holder) {
			super.onRevoked(holder);
			this.removeModifiers(holder, this.createHolderContext(holder));
		}

		@Override
		public void onTick(Entity holder) {

			Context context = createHolderContext(holder);
			int tickRate = power.getTickRate().getInt(context.forChild(".tick_rate"));

			if (context.hasAnyErrors()) {

				this.startTicks = null;
				this.endTicks = null;

				this.wasActive = false;

			}

			else {

				int ticks = holder.tickCount % tickRate;
				if (this.isActive(context)) {

					if (startTicks == null) {
						this.startTicks = ticks;
						this.endTicks = null;
					}

					else if (!wasActive && ticks == startTicks) {
						this.addModifiersTemporarily(holder, context);
						this.wasActive = true;
					}

				}

				else if (wasActive) {

					if (endTicks == null) {
						this.startTicks = null;
						this.endTicks = ticks;
					}

					else if (ticks == endTicks) {
						this.removeModifiers(holder, context);
						this.wasActive = false;
					}

				}

			}

		}

		@Override
		public boolean shouldTick(Entity holder) {
			return power.getActiveCondition().isPresent();
		}

	}

}
