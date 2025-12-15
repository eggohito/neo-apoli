package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyGlowingSelfPower extends Power {

	public static final MapCodec<ModifyGlowingSelfPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.optionalFieldOf("use_team_color", new ConstantBooleanProvider(true)).forGetter(ModifyGlowingSelfPower::getUseTeamColors))
		.and(Color.CODEC.optionalFieldOf("color", Argb.DEFAULT).forGetter(ModifyGlowingSelfPower::getColor))
		.apply(instance, ModifyGlowingSelfPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyGlowingSelfPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		BooleanProvider.STREAM_CODEC, ModifyGlowingSelfPower::getUseTeamColors,
		Color.STREAM_CODEC, ModifyGlowingSelfPower::getColor,
		ModifyGlowingSelfPower::new
	);

	private final BooleanProvider useTeamColors;
	private final Color color;

	public ModifyGlowingSelfPower(Optional<Condition> activeCondition, BooleanProvider useTeamColors, Color color) {
		super(activeCondition);
		this.useTeamColors = useTeamColors;
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_GLOWING_SELF_POWER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {

		super.validate(reporter);

		getUseTeamColors().validate(reporter.forChild(".use_team_color"));
		getColor().validate(reporter.forChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyGlowingSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyGlowingSelfPower power) {
			super(holder, power);
		}

		public boolean doesApply(Context context, boolean hasTeamColor) {
			return this.isActive(context)
				&& (!hasTeamColor || !this.shouldUseTeamColor(context));
		}

		public int getColor(Context context) {
			return this.getPower().getColor().getValue(context.forChild(".color"));
		}

		public boolean shouldUseTeamColor(Context context) {
			return this.getPower().getUseTeamColors().next(context.forChild(".use_team_color"));
		}

	}

	public static boolean modifyOutlineVisibility(Context context) {

		Entity holder = context.nullable(NeoApoliContextKeys.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class);

		for (var instance : instances) {

			ProblemReporter reporter = instance.getReporter();
			Context instanceContext = new Context.Builder(context)
				.withReporter(reporter)
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static int modifyColor(Context context, boolean hasTeamColor, int original) {

		Entity holder = context.nullable(NeoApoliContextKeys.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class);

		return modifyColor(context, instances, hasTeamColor, original);

	}

	public static int modifyColor(Context context, List<Instance> instances, boolean hasTeamColor, int original) {

		int color = original;

		for (var instance : instances) {

			ProblemReporter reporter = instance.getReporter();
			Context instanceContext = new Context.Builder(context)
				.withReporter(reporter)
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && instance.doesApply(context, hasTeamColor)) {
					color = Color.mix(color, instance.getColor(instanceContext));
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return color;

	}

	public static Context createContext(@Nullable Entity actor, Entity target) {
		return PowerTypes.MODIFY_GLOWING_SELF_POWER.contextBuilder()
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, actor)
			.add(NeoApoliContextKeys.TARGET_ENTITY, target)
			.add(NeoApoliContextKeys.THIS_ENTITY, target)
			.add(NeoApoliContextKeys.THIS_POS, target.position())
			.build(target.level());
	}

}
