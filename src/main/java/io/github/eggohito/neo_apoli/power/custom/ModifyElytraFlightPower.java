package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@EqualsAndHashCode
@Getter
public class ModifyElytraFlightPower extends Power implements Prioritized<ModifyElytraFlightPower> {

	public static final MapCodec<ModifyElytraFlightPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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

	public static boolean modify(Context context, InstanceCollection<Instance> instances, BooleanSupplier defaultValue) {

		for (var instance : instances) {

			Context instanceContext = new Context.Builder(context)
				.withValidator(instance.getValidator())
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					return instance.isAllowed(instanceContext);
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

	@ApiStatus.Internal
	public static boolean onCustomFlight(LivingEntity entity, boolean tickElytra) {

		InstanceCollection<Instance> instances = new InstanceCollection<>(entity, Instance.class);
		Context context = createContext(entity);

		boolean allow = modify(context, instances, () -> false);

		if (tickElytra && allow) {
			entity.gameEvent(GameEvent.ELYTRA_GLIDE);
		}

		return allow;

	}

	@ApiStatus.Internal
	public static boolean allowFlight(LivingEntity entity) {

		InstanceCollection<Instance> instances = new InstanceCollection<>(entity, Instance.class);
		Context context = createContext(entity);

		return modify(context, instances, () -> true);

	}

	public static Context createContext(Entity entity) {
		return PowerTypes.MODIFY_ELYTRA_FLIGHT.contextBuilder()
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

}
