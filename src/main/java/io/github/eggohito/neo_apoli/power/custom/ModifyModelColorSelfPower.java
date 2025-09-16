package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

@Getter
public class ModifyModelColorSelfPower extends Power {

	public static final MapCodec<ModifyModelColorSelfPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ModifyModelColorSelfPower::getBiEntityCondition))
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorSelfPower::getColor))
		.apply(instance, ModifyModelColorSelfPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyModelColorSelfPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BiEntityCondition.PACKET_CODEC.encode(buf, power.getBiEntityCondition());
			Color.PACKET_CODEC.encode(buf, power.getColor());
		},
		(buf, properties, condition) -> new ModifyModelColorSelfPower(properties, condition,
			BiEntityCondition.PACKET_CODEC.decode(buf),
			Color.PACKET_CODEC.decode(buf)
		)
	);

	private final BiEntityCondition biEntityCondition;
	private final Color color;

	public ModifyModelColorSelfPower(Properties properties, Optional<EntityCondition> activeCondition, BiEntityCondition biEntityCondition, Color color) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
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

		getBiEntityCondition().validate(reporter.makeChild(".bientity_condition"));
		getColor().validate(reporter.makeChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyModelColorSelfPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelColorSelfPower power) {
			super(holder, power);
		}

		public OptionalInt getColor(Context context) {

			Entity viewer = context.nullable(ContextParameters.ACTOR);
			context = this.addPowerContext(context);

			if (viewer == null || Objects.equals(viewer, holder) || this.doesApply(context)) {
				return OptionalInt.of(power.getColor().getValue(context.makeChild(".color")));
			}

			else {
				return OptionalInt.empty();
			}

		}

		public boolean doesApply(Context context) {
			return this.isActive(context)
				&& power.getBiEntityCondition().test(context.makeChild(".bientity_condition"));
		}

	}

	public static Context createContext(@NotNull Entity renderedEntity, @Nullable Entity viewer) {
		return PowerTypes.MODIFY_MODEL_COLOR_SELF.contextBuilder()
			.addNullable(ContextParameters.ACTOR, viewer)
			.add(ContextParameters.TARGET, renderedEntity)
			.add(ContextParameters.ENTITY, renderedEntity)
			.add(ContextParameters.ENTITY_POS, renderedEntity.getPos())
			.build(renderedEntity.getWorld());
	}

}
