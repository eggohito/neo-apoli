package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.TestEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.IsSneakingEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class ModifyClimbingPower extends Power {

	public static final MapCodec<ModifyClimbingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.BASE_CODEC.optionalFieldOf("holding_condition", new TestEntityCondition(new IsSneakingEntityCondition(), EntityTarget.THIS)).forGetter(ModifyClimbingPower::getHoldingCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("allow_holding", new ConstantBooleanProvider(true)).forGetter(ModifyClimbingPower::getAllowHolding))
		.apply(instance, ModifyClimbingPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyClimbingPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
		Condition.BASE_PACKET_CODEC, ModifyClimbingPower::getHoldingCondition,
		BooleanProvider.PACKET_CODEC, ModifyClimbingPower::getAllowHolding,
		ModifyClimbingPower::new
	);

	private final Condition holdingCondition;
	private final BooleanProvider allowHolding;

	public ModifyClimbingPower(Optional<Condition> activeCondition, Condition holdingCondition, BooleanProvider allowHolding) {
		super(activeCondition);
		this.holdingCondition = holdingCondition;
		this.allowHolding = allowHolding;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_CLIMBING;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getHoldingCondition().validate(reporter.makeChild(".holding_condition"));
		getAllowHolding().validate(reporter.makeChild(".allow_holding"));

	}

	public static class Instance extends Power.Instance<ModifyClimbingPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyClimbingPower power) {
			super(holder, power);
		}

		public boolean canHold(Context context) {
			return this.isActive(context)
				&& this.getPower().getAllowHolding().next(context.makeChild(".allow_holding"))
				&& this.getPower().getHoldingCondition().test(context.makeChild(".holding_condition"));
		}

	}

	public static Context createContext(Entity entity) {

		World world = entity.getWorld();
		BlockPos blockPos = entity.getBlockPos();

		return PowerTypes.MODIFY_CLIMBING.contextBuilder()
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, entity.getBlockStateAtPos())
			.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos))
			.add(ContextParameters.THIS_ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos())
			.build(world);

	}

}
