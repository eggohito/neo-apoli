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
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
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
public class ModifyModelColorOtherPower extends Power {

	public static final MapCodec<ModifyModelColorOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorOtherPower::getColor))
		.apply(instance, ModifyModelColorOtherPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyModelColorOtherPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
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

		public OptionalInt getColor(Context context) {

			Entity renderedEntity = context.nullable(ContextParameters.TARGET);
			Context colorContext = context.makeChild(".color");

			if (!Objects.equals(holder, renderedEntity) && this.isActive(context)) {
				return OptionalInt.of(power.getColor().getValue(colorContext));
			}

			else {
				return OptionalInt.empty();
			}

		}

	}

	public static Context createContext(@NotNull Entity viewer, @Nullable Entity renderedEntity) {
		return PowerTypes.MODIFY_MODEL_COLOR_OTHER.contextBuilder()
			.add(ContextParameters.ACTOR, viewer)
			.addNullable(ContextParameters.TARGET, renderedEntity)
			.add(ContextParameters.THIS_ENTITY, viewer)
			.add(ContextParameters.ENTITY_POS, viewer.getPos())
			.build(viewer.getWorld());
	}

}
