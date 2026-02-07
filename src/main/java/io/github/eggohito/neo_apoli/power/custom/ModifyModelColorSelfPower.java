package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.color.Color;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyModelColorSelfPower extends Power {

	public static final MapCodec<ModifyModelColorSelfPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorSelfPower::getColor))
		.apply(instance, ModifyModelColorSelfPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyModelColorSelfPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Color.STREAM_CODEC, ModifyModelColorSelfPower::getColor,
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
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getColor().validate(validator.forChild(".color"));
	}

	public static class Instance extends Power.Instance<ModifyModelColorSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelColorSelfPower power) {
			super(holder, power);
		}

		public Context createContext(@Nullable Entity viewer) {
			return this.createHolderContextBuilder()
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, viewer)
				.withRequired(NeoApoliContextParams.TARGET_ENTITY, holder)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_MODEL_COLOR_OTHER.keySet());
		}

		public int getColor(Context context) {
			return power.getColor().intValue(context.forChild(".color"));
		}

	}

	public static int modify(@Nullable Entity viewer, @NotNull Entity rendered, int color) {

		for (var instance : PowersComponent.getInstances(rendered, Instance.class)) {

			Context context = instance.createContext(viewer);

			if (viewer == null || Objects.equals(viewer, instance.getHolder()) || instance.isActive(context)) {
				color = Color.mix(color, instance.getColor(context));
			}

		}

		return color;

	}

}
