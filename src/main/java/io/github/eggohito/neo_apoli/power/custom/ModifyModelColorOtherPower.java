package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ModifyModelColorOtherPower extends Power {

	public static final MapCodec<ModifyModelColorOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorOtherPower::getColor))
		.apply(instance, ModifyModelColorOtherPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyModelColorOtherPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Color.PACKET_CODEC, ModifyModelColorOtherPower::getColor,
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
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getColor().validate(reporter.makeChild(".color"));
	}

	public static class Instance extends Power.Instance<ModifyModelColorOtherPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelColorOtherPower power) {
			super(holder, power);
		}

		public int getColor(Context context) {
			return power.getColor().getValue(context.makeChild(".color"));
		}

	}

	public static int modify(Context context, List<Instance> instances, int original) {

		Entity renderedEntity = context.nullable(NeoApoliContextParameters.TARGET);
		int color = original;

		for (var instance : instances) {

			ErrorReporter reporter = instance.createReporter();
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
			.add(NeoApoliContextParameters.ACTOR, viewer)
			.addNullable(NeoApoliContextParameters.TARGET, renderedEntity)
			.add(NeoApoliContextParameters.THIS_ENTITY, viewer)
			.add(NeoApoliContextParameters.ENTITY_POS, viewer.getPos())
			.build(viewer.getWorld());
	}

}
