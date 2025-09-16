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
public class ModifyModelColorOtherPower extends Power {

	public static final MapCodec<ModifyModelColorOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ModifyModelColorOtherPower::getBiEntityCondition))
		.and(Color.CODEC.fieldOf("color").forGetter(ModifyModelColorOtherPower::getColor))
		.apply(instance, ModifyModelColorOtherPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyModelColorOtherPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			BiEntityCondition.PACKET_CODEC.encode(buf, power.getBiEntityCondition());
			Color.PACKET_CODEC.encode(buf, power.getColor());
		},
		(buf, properties, condition) -> new ModifyModelColorOtherPower(properties, condition,
			BiEntityCondition.PACKET_CODEC.decode(buf),
			Color.PACKET_CODEC.decode(buf)
		)
	);

	private final BiEntityCondition biEntityCondition;
	private final Color color;

	public ModifyModelColorOtherPower(Properties properties, Optional<EntityCondition> activeCondition, BiEntityCondition biEntityCondition, Color color) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
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

		getBiEntityCondition().validate(reporter.makeChild(".bientity_condition"));
		getColor().validate(reporter.makeChild(".color"));

	}

	public static class Instance extends Power.Instance<ModifyModelColorOtherPower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyModelColorOtherPower power) {
			super(holder, power);
		}

		public OptionalInt getColor(Context context) {

			context = this.addPowerContext(context);
			Entity renderedEntity = context.nullable(ContextParameters.TARGET);

			if (!Objects.equals(holder, renderedEntity) && this.doesApply(context)) {
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

	public static Context createContext(@NotNull Entity viewer, @Nullable Entity renderedEntity) {
		return PowerTypes.MODIFY_MODEL_COLOR_OTHER.contextBuilder()
			.add(ContextParameters.ACTOR, viewer)
			.addNullable(ContextParameters.TARGET, renderedEntity)
			.add(ContextParameters.ENTITY, viewer)
			.add(ContextParameters.ENTITY_POS, viewer.getPos())
			.build(viewer.getWorld());
	}

}
