package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.IsSneakingEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class ModifyClimbingPower extends Power {

	public static final MapCodec<ModifyClimbingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityCondition.CODEC.optionalFieldOf("holding_condition", new IsSneakingEntityCondition()).forGetter(ModifyClimbingPower::getHoldingCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("allow_holding", new ConstantBooleanProvider(true)).forGetter(ModifyClimbingPower::getAllowHolding))
		.apply(instance, ModifyClimbingPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyClimbingPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			EntityCondition.PACKET_CODEC.encode(buf, power.getHoldingCondition());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getAllowHolding());
		},
		(buf, properties, condition) -> new ModifyClimbingPower(properties, condition,
			EntityCondition.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final EntityCondition holdingCondition;
	private final BooleanProvider allowHolding;

	public ModifyClimbingPower(Properties properties, Optional<EntityCondition> activeCondition, EntityCondition holdingCondition, BooleanProvider allowHolding) {
		super(properties, activeCondition);
		this.holdingCondition = holdingCondition;
		this.allowHolding = allowHolding;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_CLIMBING;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getAllowHolding().validate(reporter.makeChild(".allow_holding"));
		getHoldingCondition().validate(reporter.makeChild(".holding_condition"));

	}

	public static class Impl extends Power.Impl<ModifyClimbingPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyClimbingPower power) {
			super(holder, power);
		}

		public boolean canHold(Context context) {
			context = this.addPowerContext(context);
			return this.isActive(context)
				&& this.getPower().getAllowHolding().next(context.makeChild(".allow_holding"))
				&& this.getPower().getHoldingCondition().test(context.makeChild(".holding_condition"));
		}

	}

}
