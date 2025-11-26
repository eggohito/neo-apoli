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
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyClimbingPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Condition.STREAM_CODEC, ModifyClimbingPower::getHoldingCondition,
		BooleanProvider.STREAM_CODEC, ModifyClimbingPower::getAllowHolding,
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
		return new io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		super.validate(reporter);

		getHoldingCondition().validate(reporter.forChild(".holding_condition"));
		getAllowHolding().validate(reporter.forChild(".allow_holding"));

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

	public static boolean modify(Context context, BiPredicate<io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower.Instance, Context> tester) {

		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		List<io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower.Instance> instances = PowersComponent.getInstances(holder, io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower.Instance.class);

		return modify(context, instances, tester);

	}

	public static boolean modify(Context context, List<io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower.Instance> instances, BiPredicate<io.github.eggohito.neo_apoli.power.custom.ModifyClimbingPower.Instance, Context> tester) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
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

		Level world = entity.level();
		BlockPos blockPos = entity.blockPosition();

		return PowerTypes.MODIFY_CLIMBING.contextBuilder()
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, entity.getInBlockState())
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(blockPos))
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.ENTITY_POS, entity.position())
			.build(world);

	}

}
