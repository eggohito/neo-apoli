package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@EqualsAndHashCode
@Getter
public class ModifyElytraFlightPower extends Power implements Prioritized<ModifyElytraFlightPower> {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyElytraFlightPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyElytraFlightPower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyElytraFlightPower::getPriority))
		.apply(instance, ModifyElytraFlightPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyElytraFlightPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		BooleanProvider.STREAM_CODEC, ModifyElytraFlightPower::getAllow,
		ByteBufCodecs.INT, ModifyElytraFlightPower::getPriority,
		ModifyElytraFlightPower::new
	);

	private final BooleanProvider allow;
	private final int priority;

	public ModifyElytraFlightPower(Optional<Condition> activeCondition, BooleanProvider allow, int priority) {
		super(activeCondition);
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_ELYTRA_FLIGHT;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getAllow().validate(validator.forChild(".allow"));
	}

	public static class Instance extends Power.Instance<ModifyElytraFlightPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyElytraFlightPower power) {
			super(holder, power);
		}

		public boolean isAllowed(Context context) {
			return power.getAllow().next(context.forChild(".allow"));
		}

	}

	public static boolean modify(Entity entity, BooleanSupplier defaultValue) {

		for (var instance : new InstanceCollection<>(entity, Instance.class)) {

			Context context = instance.createHolderContext();

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					return instance.isAllowed(context);
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

}
