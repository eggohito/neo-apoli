package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.misc.AbstractModifyAttributeLegacyPower;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.AttributeModifier;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Getter
public class ModifyAttributeLegacyConditionedPower extends AbstractModifyAttributeLegacyPower {

	public static final MapCodec<ModifyAttributeLegacyConditionedPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addAttributeModifyingAndConditionFields(instance)
		.and(NumberProvider.clamped(1, Integer.MAX_VALUE).optionalFieldOf("tick_rate", new ConstantNumberProvider(20)).forGetter(ModifyAttributeLegacyConditionedPower::getTickRate))
		.apply(instance, ModifyAttributeLegacyConditionedPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyAttributeLegacyConditionedPower> PACKET_CODEC = createAttributeModifyingConditionedPacketCodec(
		(buf, power) ->
			NumberProvider.PACKET_CODEC.encode(buf, power.getTickRate()),
		(buf, properties, activeCondition, modifiers, sendUpdate) -> new ModifyAttributeLegacyConditionedPower(properties, activeCondition, modifiers, sendUpdate,
			NumberProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final NumberProvider tickRate;

	public ModifyAttributeLegacyConditionedPower(Properties properties, Optional<EntityCondition> activeCondition, List<AttributeModifier> modifiers, BooleanProvider sendUpdate, NumberProvider tickRate) {
		super(properties, activeCondition, modifiers, sendUpdate);
		this.tickRate = tickRate;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ATTRIBUTE_LEGACY_CONDITIONED;
	}

	@Override
	public AbstractModifyAttributeLegacyPower.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends AbstractModifyAttributeLegacyPower.Impl<ModifyAttributeLegacyConditionedPower> {

		private Integer startTicks;
		private Integer endTicks;

		private boolean wasActive;

		protected Impl(@NotNull Entity holder, @NotNull ModifyAttributeLegacyConditionedPower power) {
			super(holder, power);
		}

		@Override
		public void onTick() {

			Context context = this.createGenericContext();
			int tickRate = power.getTickRate().nextInt(context.makeChild(".tick_rate"));

			if (context.hasAnyErrors()) {

				this.startTicks = null;
				this.endTicks = null;

				this.wasActive = false;

			}

			else {

				int ticks = holder.age % tickRate;
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
