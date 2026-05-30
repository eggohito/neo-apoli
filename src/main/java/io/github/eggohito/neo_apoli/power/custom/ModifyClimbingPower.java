package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.IsEntitySneakingCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.BlockContextParameter;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.exception.PosOutOfBoundsException;
import io.github.eggohito.neo_apoli.exception.PosUnloadedException;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiPredicate;

@EqualsAndHashCode
@Getter
public class ModifyClimbingPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();
	public static final Context.Parameter<CachedBlock> CLIMBED_BLOCK = NeoApoliContextParams.registerInternal("climbed_block", BlockContextParameter::new);

	public static final MapCodec<ModifyClimbingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("holding_condition", new IsEntitySneakingCondition(NeoApoliContextParams.THIS_ENTITY)).forGetter(ModifyClimbingPower::getHoldingCondition))
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
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_CLIMBING;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		getHoldingCondition().validate(validator.forChild(".holding_condition"));
		getAllowHolding().validate(validator.forChild(".allow_holding"));

	}

	public static class Instance extends Power.Instance<ModifyClimbingPower> {

		protected Instance(@NotNull ModifyClimbingPower power) {
			super(power);
		}

		@Override
		public Context.Builder createHolderContextBuilder(Entity holder) {

			Level level = holder.level();
			BlockPos blockPos = holder.blockPosition();

			return super.createHolderContextBuilder(holder).withRequired(CLIMBED_BLOCK, CachedBlock.fromLoadedPos(level, blockPos));

		}

		public boolean canHold(Context context) {
			return this.isActive(context)
				&& power.getAllowHolding().getBoolean(context.forChild(".allow_holding"))
				&& power.getHoldingCondition().test(context.forChild(".holding_condition"));
		}

	}

	public static boolean modify(Entity entity, BiPredicate<Instance, Context> tester) {

		for (var instance : Powers.getInstances(entity, Instance.class)) {

			try {

				Context context = instance.createHolderContext(entity);

				if (VISITOR.push(instance) && tester.test(instance, context)) {
					return true;
				}

			}

			catch (PosUnloadedException | PosOutOfBoundsException ignored) {
				//  No-op; just need to soft error
			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return false;

	}

}
