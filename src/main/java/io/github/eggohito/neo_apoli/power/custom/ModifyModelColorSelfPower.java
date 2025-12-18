package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
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
import java.util.Objects;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyModelColorSelfPower extends Power {

	public static final MapCodec<ModifyModelColorSelfPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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

		public int getColor(Context context) {
			return power.getColor().getValue(context.forChild(".color"));
		}

	}

	public static int modify(Context context, List<Instance> instances, int original) {

		Entity viewer = context.nullable(NeoApoliContextKeys.ACTOR_ENTITY);
		int color = original;

		for (var instance : instances) {

			Context.Validator validator = instance.getValidator();
			Context instanceContext = new Context.Builder(context)
				.withValidator(validator)
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && (viewer == null || Objects.equals(viewer, instance.getHolder()) || instance.isActive(instanceContext))) {
					color = Color.mix(color, instance.getColor(instanceContext));
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return color;

	}

	public static Context createContext(@NotNull Entity renderedEntity, @Nullable Entity viewer) {
		return PowerTypes.MODIFY_MODEL_COLOR_SELF.contextBuilder()
			.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, viewer)
			.add(NeoApoliContextKeys.TARGET_ENTITY, renderedEntity)
			.add(NeoApoliContextKeys.THIS_ENTITY, renderedEntity)
			.add(NeoApoliContextKeys.THIS_POS, renderedEntity.position())
			.build(renderedEntity.level());
	}

}
