package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.custom.NothingAction;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingReference;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.KeyBound;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class TogglePower extends Power implements KeyBound {

	public static final MapCodec<TogglePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Action.BASE_CODEC.optionalFieldOf("action", new NothingAction()).forGetter(TogglePower::getAction))
		.and(KeyBindingReference.CODEC.fieldOf("key").forGetter(TogglePower::getKey))
		.and(BooleanProvider.CODEC.optionalFieldOf("retain_state", new ConstantBooleanProvider(true)).forGetter(TogglePower::getRetainState))
		.and(BooleanProvider.CODEC.optionalFieldOf("active_by_default", new ConstantBooleanProvider(true)).forGetter(TogglePower::getActiveByDefault))
		.apply(instance, TogglePower::new));

	public static final PacketCodec<RegistryByteBuf, TogglePower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
		Action.BASE_PACKET_CODEC, TogglePower::getAction,
		KeyBindingReference.PACKET_CODEC, TogglePower::getKey,
		BooleanProvider.PACKET_CODEC, TogglePower::getRetainState,
		BooleanProvider.PACKET_CODEC, TogglePower::getActiveByDefault,
		TogglePower::new
	);

	private final Action action;
	private final KeyBindingReference key;

	private final BooleanProvider retainState;
	private final BooleanProvider activeByDefault;

	public TogglePower(Optional<Condition> activeCondition, Action action, KeyBindingReference key, BooleanProvider retainState, BooleanProvider activeByDefault) {
		super(activeCondition);
		this.action = action;
		this.key = key;
		this.retainState = retainState;
		this.activeByDefault = activeByDefault;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.TOGGLE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public KeyBindingReference getKey() {
		return key;
	}

	public static class Instance extends Power.Instance<TogglePower> implements KeyBound.Instance {

		private boolean toggled;

		protected Instance(@NotNull Entity holder, @NotNull TogglePower power) {
			super(holder, power);
			this.toggled = power.getActiveByDefault().next(this.createHolderContext().makeChild(".active_by_default"));
		}

		@Override
		public KeyBound getKeyBound() {
			return power;
		}

		@Override
		public boolean shouldTrigger(Context context) {
			return super.isActive(context);
		}

		@Override
		public void onPress(Context context) {
			this.toggle(context);
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return ops.getBooleanValue(data)
				.ifSuccess(bool -> this.toggled = bool)
				.map(bool -> Unit.INSTANCE);
		}

		@Override
		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.createBoolean(this.toggled));
		}

		@Override
		public void onTick() {

			Context context = this.createHolderContext();

			if (toggled && !super.isActive(context)) {
				this.toggle(context);
			}

		}

		@Override
		public boolean shouldTick() {
			return power.getActiveCondition().isPresent()
				&& !power.getRetainState().next(this.createHolderContext().makeChild(".retain_state"));
		}

		@Override
		public boolean isActive(Context context) {
			return toggled;
		}

		protected void toggle(Context context) {

			if (!holder.getWorld().isClient()) {

				this.toggled = !toggled;
				this.syncData();

				power.getAction().execute(context.makeChild(".action"));

			}

		}

	}

}
