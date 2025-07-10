package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@Getter
public class EntityModelColorPower extends Power {

	public static final MapCodec<EntityModelColorPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("alpha").forGetter(EntityModelColorPower::getAlpha))
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("red").forGetter(EntityModelColorPower::getRed))
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("green").forGetter(EntityModelColorPower::getGreen))
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("blue").forGetter(EntityModelColorPower::getBlue))
		.apply(instance, EntityModelColorPower::new));

	public static final PacketCodec<RegistryByteBuf, EntityModelColorPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, entityModelColorPower) -> {
			NumberProvider.PACKET_CODEC.encode(buf, entityModelColorPower.getAlpha());
			NumberProvider.PACKET_CODEC.encode(buf, entityModelColorPower.getRed());
			NumberProvider.PACKET_CODEC.encode(buf, entityModelColorPower.getGreen());
			NumberProvider.PACKET_CODEC.encode(buf, entityModelColorPower.getBlue());
		},
		(buf, properties, condition) -> new EntityModelColorPower(properties, condition,
			NumberProvider.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf),
			NumberProvider.PACKET_CODEC.decode(buf)
		)
	);

	private final NumberProvider alpha;
	private final NumberProvider red;
	private final NumberProvider green;
	private final NumberProvider blue;

	public EntityModelColorPower(Properties properties, EntityCondition activeCondition, NumberProvider alpha, NumberProvider red, NumberProvider green, NumberProvider blue) {
		super(properties, activeCondition);
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.ENTITY_MODEL_COLOR;
	}

	@Override
	public Power.Impl<?> createImpl(Entity holder) {
		return new Impl(holder, this);
	}

	public static class Impl extends Power.Impl<EntityModelColorPower> {

		protected Impl(@NotNull Entity holder, @NotNull EntityModelColorPower power) {
			super(holder, power);
		}

		public Optional<Float> getAlpha() {

			Context alphaContext = this.genericContext().makeChild(".alpha");
			float alpha = power.getAlpha().nextFloat(alphaContext);

			if (alphaContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(alpha);
			}

		}

		public Optional<Float> getRed() {

			Context redContext = this.genericContext().makeChild(".red");
			float red = power.getRed().nextFloat(redContext);

			if (redContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(red);
			}

		}

		public Optional<Float> getGreen() {

			Context greenContext = this.genericContext().makeChild(".green");
			float green = power.getGreen().nextFloat(greenContext);

			if (greenContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(green);
			}

		}

		public Optional<Float> getBlue() {

			Context blueContext = this.genericContext().makeChild(".blue");
			float blue = power.getBlue().nextFloat(blueContext);

			if (blueContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(blue);
			}

		}

		public boolean isActive() {
			return this.isActive(this.genericContext());
		}

	}

}
