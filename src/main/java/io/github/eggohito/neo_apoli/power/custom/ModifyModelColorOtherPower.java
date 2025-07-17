package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.color.Argb;
import io.github.eggohito.neo_apoli.util.color.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

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

	public ModifyModelColorOtherPower(Properties properties, EntityCondition activeCondition, BiEntityCondition biEntityCondition, Color color) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_MODEL_COLOR_OTHER;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<ModifyModelColorOtherPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyModelColorOtherPower power) {
			super(holder, power);
		}

		public Optional<Argb> getColorForEntity(@Nullable Entity renderedEntity) {

			Context context = this.contextBuilder()
				.add(ContextParameters.ACTOR, holder)
				.addNullable(ContextParameters.TARGET, renderedEntity)
				.build(holder.getWorld());

			if (!Objects.equals(holder, renderedEntity) && this.isActive(context) && power.getBiEntityCondition().test(context.makeChild(".bientity_condition"))) {
				return Optional.of(this.power.getColor().toArgb(context.makeChild(".color")));
			}

			else {
				return Optional.empty();
			}

		}

	}

}
