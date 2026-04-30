package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyJumpPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyJumpPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyJumpPower::getModifiers))
		.apply(instance, ModifyJumpPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyJumpPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Modifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ModifyJumpPower::getModifiers,
		ModifyJumpPower::new
	);

	private final List<Modifier> modifiers;

	public ModifyJumpPower(Optional<Condition> activeCondition, List<Modifier> modifiers) {
		super(activeCondition);
		this.modifiers = modifiers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_JUMP;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		ContextHelper.validateAll(getModifiers(), validator, index -> ".modifiers[" + index + "]");
	}

	public static class Instance extends Power.Instance<ModifyJumpPower> {

		protected Instance(@NotNull ModifyJumpPower power) {
			super(power);
		}

		public List<Modifier.Entry> getModifiers(Context context) {

			List<Modifier.Entry> result = new ObjectArrayList<>();
			MiscUtil.iterateList(power.getModifiers(), (index, modifier) -> result.add(Modifier.entry(modifier, context.forChild(".modifiers[" + index + "]"))));

			return result;

		}

	}

	public static float modify(LivingEntity entity, float jumpPower) {

		List<Modifier.Entry> entries = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(entity, Instance.class)) {

			Context context = instance.createHolderContext(entity);

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					entries.addAll(instance.getModifiers(context));
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		ModifyValue.EVENT.invoker().beforeModified(PowerTypes.MODIFY_JUMP, entries, jumpPower);
		return (float) Modifier.applyAll(entries, jumpPower);

	}

}
