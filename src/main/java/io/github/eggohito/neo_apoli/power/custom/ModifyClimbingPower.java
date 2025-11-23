package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
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
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@Getter
public class ModifyClimbingPower extends Power {

	public static final MapCodec<ModifyClimbingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("holding_condition", new TestEntityCondition(new IsSneakingEntityCondition(), EntityTarget.THIS)).forGetter(ModifyClimbingPower::getHoldingCondition))
		.and(BooleanProvider.CODEC.optionalFieldOf("allow_holding", new ConstantBooleanProvider(true)).forGetter(ModifyClimbingPower::getAllowHolding))
		.apply(instance, ModifyClimbingPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyClimbingPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Condition.PACKET_CODEC, ModifyClimbingPower::getHoldingCondition,
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

	public static boolean modify(Context context, BiPredicate<Instance, Context> tester) {

		Entity holder = context.required(NeoApoliContextParameters.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class);

		return modify(context, instances, tester);

	}

	public static boolean modify(Context context, List<Instance> instances, BiPredicate<Instance, Context> tester) {

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && tester.test(instance, instanceContext)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static Context createContext(Entity entity) {

		World world = entity.getWorld();
		BlockPos blockPos = entity.getBlockPos();

		return PowerTypes.MODIFY_CLIMBING.contextBuilder()
			.add(NeoApoliContextParameters.BLOCK_POS, blockPos)
			.add(NeoApoliContextParameters.BLOCK_STATE, entity.getBlockStateAtPos())
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos))
			.add(NeoApoliContextParameters.THIS_ENTITY, entity)
			.add(NeoApoliContextParameters.ENTITY_POS, entity.getPos())
			.build(world);

	}

}
