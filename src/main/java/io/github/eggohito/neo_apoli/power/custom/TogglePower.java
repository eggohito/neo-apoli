package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.action.meta.entity.NothingEntityAction;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.keybinding.KeyBindingReference;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.KeyBound;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.meta.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TogglePower extends Power implements KeyBound {

	public static final MapCodec<TogglePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(EntityAction.CODEC.optionalFieldOf("entity_action", new NothingEntityAction()).forGetter(TogglePower::getEntityAction))
		.and(KeyBindingReference.CODEC.fieldOf("key").forGetter(TogglePower::getKeyBindingReference))
		.and(BooleanProvider.CODEC.optionalFieldOf("retain_state", new ConstantBooleanProvider(true)).forGetter(TogglePower::getRetainState))
		.and(BooleanProvider.CODEC.optionalFieldOf("active_by_default", new ConstantBooleanProvider(true)).forGetter(TogglePower::getActiveByDefault))
		.apply(instance, TogglePower::new));

	public static final PacketCodec<RegistryByteBuf, TogglePower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			EntityAction.PACKET_CODEC.encode(buf, power.getEntityAction());
			KeyBindingReference.PACKET_CODEC.encode(buf, power.getKeyBindingReference());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getRetainState());
			BooleanProvider.PACKET_CODEC.encode(buf, power.getActiveByDefault());
		},
		(buf, properties, activeCondition) -> new TogglePower(properties, activeCondition,
			EntityAction.PACKET_CODEC.decode(buf),
			KeyBindingReference.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf),
			BooleanProvider.PACKET_CODEC.decode(buf)
		)
	);

	@Getter
	private final EntityAction entityAction;
	private final KeyBindingReference key;

	@Getter
	private final BooleanProvider retainState;
	@Getter
	private final BooleanProvider activeByDefault;

	public TogglePower(Properties properties, Optional<EntityCondition> activeCondition, EntityAction entityAction, KeyBindingReference key, BooleanProvider retainState, BooleanProvider activeByDefault) {
		super(properties, activeCondition);
		this.entityAction = entityAction;
		this.key = key;
		this.retainState = retainState;
		this.activeByDefault = activeByDefault;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.TOGGLE;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	@Override
	public KeyBindingReference getKeyBindingReference() {
		return key;
	}

	public static class Impl extends Power.Impl<TogglePower> implements KeyBound.Impl {

		private boolean toggled;

		protected Impl(@NotNull Entity holder, @NotNull TogglePower power) {
			super(holder, power);
			this.toggled = power.getActiveByDefault().next(this.createGenericContext().makeChild(".active_by_default"));
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

			Context context = this.createGenericContext();

			if (toggled && !super.isActive(context)) {
				this.toggle(context);
			}

		}

		@Override
		public boolean shouldTick() {
			return power.getActiveCondition().isPresent()
				&& !power.getRetainState().next(this.createGenericContext().makeChild(".retain_state"));
		}

		@Override
		public boolean isActive(Context context) {
			return toggled;
		}

		protected void toggle(Context context) {

			if (!holder.getWorld().isClient()) {

				this.toggled = !toggled;
				this.syncData();

				power.getEntityAction().execute(context.makeChild(".entity_action"));

			}

		}

	}

}
