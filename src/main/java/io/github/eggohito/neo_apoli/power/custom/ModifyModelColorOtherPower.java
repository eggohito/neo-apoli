package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ModifyModelColorOtherPower extends Power {

	public static final MapCodec<ModifyModelColorOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorOtherPower::getColor))
		.apply(instance, ModifyModelColorOtherPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyModelColorOtherPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Color.STREAM_CODEC, ModifyModelColorOtherPower::getColor,
		ModifyModelColorOtherPower::new
	);

	private final Color color;

	public ModifyModelColorOtherPower(Optional<Condition> activeCondition, Color color) {
		super(activeCondition);
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_MODEL_COLOR_OTHER;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new io.github.eggohito.neo_apoli.power.custom.ModifyModelColorOtherPower.Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		super.validate(reporter);
		getColor().validate(reporter.forChild(".color"));
	}

	public static class Instance extends Power.Instance<ModifyModelColorOtherPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelColorOtherPower power) {
			super(holder, power);
		}

		public int getColor(Context context) {
			return power.getColor().getValue(context.makeChild(".color"));
		}

	}

	public static int modify(Context context, List<io.github.eggohito.neo_apoli.power.custom.ModifyModelColorOtherPower.Instance> instances, int original) {

		Entity renderedEntity = context.nullable(NeoApoliContextKeys.TARGET);
		int color = original;

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && !Objects.equals(instance.getHolder(), renderedEntity) && instance.isActive(instanceContext)) {
					color = Color.mix(color, instance.getColor(instanceContext));
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return color;

	}

	public static Context createContext(@NotNull Entity viewer, @Nullable Entity renderedEntity) {
		return PowerTypes.MODIFY_MODEL_COLOR_OTHER.contextBuilder()
			.add(NeoApoliContextKeys.ACTOR, viewer)
			.addNullable(NeoApoliContextKeys.TARGET, renderedEntity)
			.add(NeoApoliContextKeys.THIS_ENTITY, viewer)
			.add(NeoApoliContextKeys.ENTITY_POS, viewer.position())
			.build(viewer.level());
	}

}
