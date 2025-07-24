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
import io.github.eggohito.neo_apoli.util.context.ContextTypes;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.context.ContextType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

@Getter
public class ModifyModelColorSelfPower extends Power {

	public static final ContextType CONTEXT_TYPE = ContextTypes.merge(ContextTypes.GENERIC, ContextTypes.BIENTITY, ContextTypes.ENTITY);

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

	public ModifyModelColorSelfPower(Properties properties, EntityCondition activeCondition, BiEntityCondition biEntityCondition, Color color) {
		super(properties, activeCondition);
		this.biEntityCondition = biEntityCondition;
		this.color = color;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_MODEL_COLOR_SELF;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<ModifyModelColorSelfPower> {

		protected Impl(@NotNull Entity holder, @NotNull ModifyModelColorSelfPower power) {
			super(holder, power);
		}

		public Optional<Argb> getColorWithoutViewer() {
			return getColorWithViewer(null);
		}

		public Optional<Argb> getColorWithViewer(@Nullable Entity viewer) {

			Context context = this.createContextBuilder()
				.addNullable(ContextParameters.ACTOR, viewer)
				.add(ContextParameters.TARGET, holder)
				.build(holder.getWorld());

			if (viewer == null || Objects.equals(holder, viewer) || (this.isActive(context) && this.power.getBiEntityCondition().test(context.makeChild(".bientity_condition")))) {
				return Optional.of(power.getColor().toArgb(context.makeChild(".color")));
			}

			else {
				return Optional.empty();
			}

		}

	}

}
