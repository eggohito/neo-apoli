package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.color.custom.Argb;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyGlowingOtherPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyGlowingOtherPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.optionalFieldOf("use_team_color", new ConstantBooleanProvider(true)).forGetter(ModifyGlowingOtherPower::getUseTeamColor))
		.and(Color.CODEC.optionalFieldOf("color", Argb.DEFAULT).forGetter(ModifyGlowingOtherPower::getColor))
		.apply(instance, ModifyGlowingOtherPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyGlowingOtherPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		BooleanProvider.STREAM_CODEC, ModifyGlowingOtherPower::getUseTeamColor,
		Color.STREAM_CODEC, ModifyGlowingOtherPower::getColor,
		ModifyGlowingOtherPower::new
	);

	private final BooleanProvider useTeamColor;
	private final Color color;

	public ModifyGlowingOtherPower(Optional<Condition> activeCondition, BooleanProvider useTeamColor, Color color) {
		super(activeCondition);
		this.useTeamColor = useTeamColor;
		this.color = color;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_GLOWING_OTHER;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		getUseTeamColor().validate(validator.forChild(".use_team_color"));
		getColor().validate(validator.forChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyGlowingOtherPower> {

		protected Instance(@NotNull ModifyGlowingOtherPower power) {
			super(power);
		}

		public Context createContext(Entity holder, Entity rendered) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.ACTOR_ENTITY, holder)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, rendered)
				.buildWithRequirements(holder.level(), NeoApoliPowerTypes.MODIFY_GLOWING_OTHER.keySet());
		}

		public boolean doesApply(Context context, boolean hasTeamColor) {
			return this.isActive(context)
				&& (!hasTeamColor || !this.shouldUseTeamColor(context));
		}

		public boolean shouldUseTeamColor(Context context) {
			return power.getUseTeamColor().nextBoolean(context.forChild(".use_team_color"));
		}

		public int getColor(Context context) {
			return power.getColor().intValue(context.forChild(".color"));
		}

	}

	public static boolean modifyGlowing(Entity viewer, @NotNull Entity rendered) {

		for (var instance : Powers.getInstances(viewer, Instance.class)) {

			Context context = instance.createContext(viewer, rendered);

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					return true;
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return false;

	}

	public static int modifyColor(Entity viewer, @NotNull Entity rendered, boolean hasTeamColor, int color) {

		for (var instance : Powers.getInstances(viewer, Instance.class)) {

			Context context = instance.createContext(viewer, rendered);

			try {

				if (VISITOR.push(instance) && instance.doesApply(context, hasTeamColor)) {
					color = Color.mix(color, instance.getColor(context));
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return color;

	}

}
