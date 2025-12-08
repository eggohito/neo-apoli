package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.AttributeModifying;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.util.AttributedAttributeModifier;
import io.github.eggohito.neo_apoli.util.context.Context;
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
public class ModifyAttributeLegacyConditionalPower extends AttributeModifying {

	public static final MapCodec<ModifyAttributeLegacyConditionalPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addConditionalAttributeModifyingAndFields(instance)
		.and(NumberProvider.clamped(1, Integer.MAX_VALUE).optionalFieldOf("tick_rate", new ConstantNumberProvider(20)).forGetter(ModifyAttributeLegacyConditionalPower::getTickRate))
		.apply(instance, ModifyAttributeLegacyConditionalPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyAttributeLegacyConditionalPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		NeoApoliStreamCodecs.ATTRIBUTE_MODIFIERS, AttributeModifying::getModifiers,
		BooleanProvider.STREAM_CODEC, AttributeModifying::getSendUpdate,
		NumberProvider.STREAM_CODEC, ModifyAttributeLegacyConditionalPower::getTickRate,
		ModifyAttributeLegacyConditionalPower::new
	);

	private final NumberProvider tickRate;

	public ModifyAttributeLegacyConditionalPower(Optional<Condition> activeCondition, List<AttributedAttributeModifier> modifiers, BooleanProvider sendUpdate, NumberProvider tickRate) {
		super(activeCondition, modifiers, sendUpdate);
		this.tickRate = tickRate;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ATTRIBUTE_LEGACY_CONDITIONAL;
	}

	@Override
	public AttributeModifying.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	public static class Instance extends AttributeModifying.Instance<ModifyAttributeLegacyConditionalPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Instance(@NotNull Entity holder, @NotNull ModifyAttributeLegacyConditionalPower power) {
			super(holder, power);
		}

		@Override
		public void onTick() {

			Context context = createHolderContext();
			int tickRate = power.getTickRate().nextInt(context.makeChild(".tick_rate"));

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
						this.addModifiersTemporarily(context);
						this.wasActive = true;
					}

				}

				else if (wasActive) {

					if (endTicks == null) {
						this.startTicks = null;
						this.endTicks = ticks;
					}

					else if (ticks == endTicks) {
						this.removeModifiers(context);
						this.wasActive = false;
					}

				}

			}

		}

		@Override
		public boolean shouldTick() {
			return true;
		}

	}

}
