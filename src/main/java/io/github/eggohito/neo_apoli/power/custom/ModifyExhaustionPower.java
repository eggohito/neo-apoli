package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.PowerModifyEvents;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextHelper;
import io.github.eggohito.neo_apoli.modifier.Modifier;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyExhaustionPower extends Power {

	public static final MapCodec<ModifyExhaustionPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyExhaustionPower::getModifiers))
		.apply(instance, ModifyExhaustionPower::new)
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyExhaustionPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Modifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ModifyExhaustionPower::getModifiers,
		ModifyExhaustionPower::new
	);

	private final List<Modifier> modifiers;

	public ModifyExhaustionPower(Optional<Condition> activeCondition, List<Modifier> modifiers) {
		super(activeCondition);
		this.modifiers = modifiers;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_EXHAUSTION;
	}

	@Override
	public Instance createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		ContextHelper.validateAll(getModifiers(), validator, index -> ".modifiers[" + index + "]");
	}

	public static class Instance extends Power.Instance<ModifyExhaustionPower> {

		protected Instance(@NotNull ModifyExhaustionPower power) {
			super(power);
		}

		public List<Modifier> getModifiers() {
			return power.getModifiers();
		}

	}

	public static float modify(Player player, float exhaustion) {

		List<Modifier.Operation> operations = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(player, Instance.class)) {

			Context context = instance.createHolderContext(player);

			if (instance.isActive(context)) {
				MiscUtil.iterateList(instance.getModifiers(), (index, modifier) -> operations.add(Modifier.operation(modifier, context.forChild(".modifiers[" + index + "]"))));
			}

		}

		PowerModifyEvents.NUMBER.invoker().beforeModified(NeoApoliPowerTypes.MODIFY_EXHAUSTION, operations, exhaustion);
		return (float) Modifier.applyAll(operations, exhaustion);

	}

}
