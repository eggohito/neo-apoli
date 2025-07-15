package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.util.Color;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

//	TODO: Create more custom classes for color types
@Getter
public class ModifyModelColorOtherPower extends Power {

	public static final MapCodec<ModifyModelColorOtherPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance)
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("alpha").forGetter(ModifyModelColorOtherPower::getAlpha))
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("red").forGetter(ModifyModelColorOtherPower::getRed))
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("green").forGetter(ModifyModelColorOtherPower::getGreen))
		.and(NumberProvider.clamped(0.0, 1.0).fieldOf("blue").forGetter(ModifyModelColorOtherPower::getBlue))
		.apply(instance, ModifyModelColorOtherPower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyModelColorOtherPower> PACKET_CODEC = createCommonConditionedPacketCodec(
		(buf, power) -> {
			NumberProvider.PACKET_CODEC.encode(buf, power.getAlpha());
			NumberProvider.PACKET_CODEC.encode(buf, power.getRed());
			NumberProvider.PACKET_CODEC.encode(buf, power.getGreen());
			NumberProvider.PACKET_CODEC.encode(buf, power.getBlue());
		},
		(buf, properties, condition) -> new ModifyModelColorOtherPower(properties, condition,
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

	public ModifyModelColorOtherPower(Properties properties, EntityCondition activeCondition, NumberProvider alpha, NumberProvider red, NumberProvider green, NumberProvider blue) {
		super(properties, activeCondition);
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
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

		public Optional<Color> getColor(@Nullable Entity renderedEntity) {

			Optional<Entity> wrappedRenderedEntity = Optional.ofNullable(renderedEntity);
			Context context = this.contextBuilder()
				.addOptional(ContextParameters.THIS_ENTITY, wrappedRenderedEntity)
				.addOptional(ContextParameters.POSITION, wrappedRenderedEntity.map(Entity::getPos))
				.add(ContextParameters.ACTOR, holder)
				.addOptional(ContextParameters.TARGET, wrappedRenderedEntity)
				.build(holder.getWorld());

			if (this.isActive(context)) {

				float alpha = this.getAlpha(context).orElse(1.0F);
				float red = this.getRed(context).orElse(1.0F);
				float green = this.getGreen(context).orElse(1.0F);
				float blue = this.getBlue(context).orElse(1.0F);

				return Optional.of(new Color(alpha, red, green, blue));

			}

			else {
				return Optional.empty();
			}

		}

		public Optional<Float> getAlpha(Context context) {

			Context alphaContext = context.makeChild(".alpha");
			float alpha = power.getAlpha().nextFloat(alphaContext);

			if (alphaContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(alpha);
			}

		}

		public Optional<Float> getRed(Context context) {

			Context redContext = context.makeChild(".red");
			float red = power.getRed().nextFloat(redContext);

			if (redContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(red);
			}

		}

		public Optional<Float> getGreen(Context context) {

			Context greenContext = context.makeChild(".green");
			float green = power.getGreen().nextFloat(greenContext);

			if (greenContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(green);
			}

		}

		public Optional<Float> getBlue(Context context) {

			Context blueContext = context.makeChild(".blue");
			float blue = power.getBlue().nextFloat(blueContext);

			if (blueContext.hasErrors()) {
				return Optional.empty();
			}

			else {
				return Optional.of(blue);
			}

		}

	}

}
