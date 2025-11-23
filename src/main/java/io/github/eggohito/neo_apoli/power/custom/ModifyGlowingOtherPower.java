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
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Getter
public class ModifyGlowingOtherPower extends Power {

	public static final MapCodec<ModifyGlowingOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.optionalFieldOf("use_team_color", new ConstantBooleanProvider(true)).forGetter(ModifyGlowingOtherPower::getUseTeamColor))
		.and(Color.CODEC.optionalFieldOf("color", Argb.DEFAULT).forGetter(ModifyGlowingOtherPower::getColor))
		.apply(instance, ModifyGlowingOtherPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyGlowingOtherPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		BooleanProvider.PACKET_CODEC, ModifyGlowingOtherPower::getUseTeamColor,
		Color.PACKET_CODEC, ModifyGlowingOtherPower::getColor,
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
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_GLOWING_OTHER_POWER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {

		super.validate(reporter);

		getUseTeamColor().validate(reporter.makeChild(".use_team_color"));
		getColor().validate(reporter.makeChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyGlowingOtherPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyGlowingOtherPower power) {
			super(holder, power);
		}

		public boolean doesApply(Context context, boolean hasTeamColor) {
			return this.isActive(context)
				&& (!hasTeamColor || !this.shouldUseTeamColor(context));
		}

		public int getColor(Context context) {
			return this.getPower().getColor().getValue(context.makeChild(".color"));
		}

		public boolean shouldUseTeamColor(Context context) {
			return this.getPower().getUseTeamColor().next(context.makeChild(".use_team_color"));
		}

	}

	public static boolean modifyOutlineVisibility(Context context) {

		Entity holder = context.nullable(NeoApoliContextParameters.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class);

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

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

		Entity holder = context.nullable(NeoApoliContextParameters.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class);

		return modifyColor(context, instances, hasTeamColor, original);

	}

	public static int modifyColor(Context context, List<Instance> instances, boolean hasTeamColor, int original) {

		int color = original;

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

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

	public static Context createContext(Entity actor, Entity target) {
		return PowerTypes.MODIFY_GLOWING_SELF_POWER.contextBuilder()
			.add(NeoApoliContextParameters.ACTOR, actor)
			.add(NeoApoliContextParameters.TARGET, target)
			.add(NeoApoliContextParameters.THIS_ENTITY, actor)
			.add(NeoApoliContextParameters.ENTITY_POS, actor.getPos())
			.build(target.getWorld());
	}

}
