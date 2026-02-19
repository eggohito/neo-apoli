package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyGlowingSelfPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyGlowingSelfPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
		return PowerTypes.MODIFY_GLOWING_SELF;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {

		super.validate(validator);

		getUseTeamColors().validate(validator.forChild(".use_team_color"));
		getColor().validate(validator.forChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyGlowingSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyGlowingSelfPower power) {
			super(holder, power);
		}

		public Context createContext(@Nullable Entity viewer) {
			return this.createHolderContextBuilder()
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, viewer)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, holder)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_GLOWING_SELF.keySet());
		}

		public boolean doesApply(Context context, boolean hasTeamColor) {
			return this.isActive(context)
				&& (!hasTeamColor || !this.shouldUseTeamColor(context));
		}

		public boolean shouldUseTeamColor(Context context) {
			return this.getPower().getUseTeamColors().nextBoolean(context.forChild(".use_team_color"));
		}

		public int getColor(Context context) {
			return this.getPower().getColor().intValue(context.forChild(".color"));
		}

	}

	public static boolean modifyGlowing(Entity viewer, @NotNull Entity rendered) {

		for (var instance : PowersComponent.getInstances(rendered, Instance.class)) {

			Context context = instance.createContext(viewer);

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

		for (var instance : PowersComponent.getInstances(rendered, Instance.class)) {

			Context context = instance.createContext(viewer);

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
