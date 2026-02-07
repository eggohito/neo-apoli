package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyAirSpeedPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyAirSpeedPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyAirSpeedPower::getModifiers))
		.apply(instance, ModifyAirSpeedPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyAirSpeedPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		ByteBufCodecs.collection(ObjectArrayList::new, Modifier.STREAM_CODEC), ModifyAirSpeedPower::getModifiers,
		ModifyAirSpeedPower::new
	);

	private final List<Modifier> modifiers;

	public ModifyAirSpeedPower(Optional<Condition> activeCondition, List<Modifier> modifiers) {
		super(activeCondition);
		this.modifiers = modifiers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_AIR_SPEED;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		MiscUtil.iterate(getModifiers(), (index, modifier) -> modifier.validate(validator.forChild(".modifiers[" + index + "]")));
	}

	public static class Instance extends Power.Instance<ModifyAirSpeedPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyAirSpeedPower power) {
			super(holder, power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

	}

	public static float modify(Entity entity, float airSpeed) {

		List<Modifier.Entry> entries = new ObjectArrayList<>();

		for (var instance : PowersComponent.getInstances(entity, Instance.class)) {

			Context context = instance.createHolderContext();

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					MiscUtil.iterate(instance.getModifiers(), (index, modifier) -> entries.add(Modifier.entry(modifier, context.forChild(".modifiers[" + index + "]"))));
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		ModifyValue.EVENT.invoker().beforeModified(PowerTypes.MODIFY_AIR_SPEED, entries, airSpeed);
		return (float) Modifier.applyAll(entries, airSpeed);

	}

}
