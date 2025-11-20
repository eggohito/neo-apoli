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
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

@Getter
public class ModifyModelColorSelfPower extends Power {

	public static final MapCodec<ModifyModelColorSelfPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorSelfPower::getColor))
		.apply(instance, ModifyModelColorSelfPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyModelColorSelfPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Color.PACKET_CODEC, ModifyModelColorSelfPower::getColor,
		ModifyModelColorSelfPower::new
	);

	private final Color color;

	public ModifyModelColorSelfPower(Optional<Condition> activeCondition, Color color) {
		super(activeCondition);
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_MODEL_COLOR_SELF;
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

	public static class Instance extends Power.Instance<ModifyModelColorSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelColorSelfPower power) {
			super(holder, power);
		}

		public OptionalInt getColor(Context context) {

			Entity viewer = context.nullable(NeoApoliContextParameters.ACTOR);
			Context colorContext = context.makeChild(".color");

			if (viewer == null || Objects.equals(viewer, holder) || this.isActive(context)) {
				return OptionalInt.of(power.getColor().getValue(colorContext));
			}

			else {
				return OptionalInt.empty();
			}

		}

	}

	public static Context createContext(@NotNull Entity renderedEntity, @Nullable Entity viewer) {
		return PowerTypes.MODIFY_MODEL_COLOR_SELF.contextBuilder()
			.addNullable(NeoApoliContextParameters.ACTOR, viewer)
			.add(NeoApoliContextParameters.TARGET, renderedEntity)
			.add(NeoApoliContextParameters.THIS_ENTITY, renderedEntity)
			.add(NeoApoliContextParameters.ENTITY_POS, renderedEntity.getPos())
			.build(renderedEntity.getWorld());
	}

}
