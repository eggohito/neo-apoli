package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.color.Color;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyModelColorOtherPower extends Power {

	public static final MapCodec<ModifyModelColorOtherPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getColor().validate(validator.forChild(".color"));
	}

	public static class Instance extends Power.Instance<ModifyModelColorOtherPower> {

		protected Instance(@NotNull ModifyModelColorOtherPower power) {
			super(power);
		}

		public Context createContext(@NotNull Entity holder, @NotNull Entity rendered) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.ACTOR_ENTITY, holder)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, rendered)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_MODEL_COLOR_OTHER.keySet());
		}

		public int getColor(Context context) {
			return power.getColor().intValue(context.forChild(".color"));
		}

	}

	public static int modify(@NotNull Entity viewer, @NotNull Entity rendered, int color) {

		for (var instance : Powers.getInstances(viewer, Instance.class)) {

			Context context = instance.createContext(viewer, rendered);

			if (!Objects.equals(viewer, rendered) && instance.isActive(context)) {
				color = Color.mix(color, instance.getColor(context));
			}

		}

		return color;

	}

}
