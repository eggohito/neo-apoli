package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.TestEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.IsSneakingEntityCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.*;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Getter
public class PhasingPower extends Power {

	public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("phase_down_condition", new TestEntityCondition(new IsSneakingEntityCondition(), EntityTarget.THIS)).forGetter(PhasingPower::getPhaseDownCondition))
		.and(RenderType.CODEC.optionalFieldOf("render_type", RenderType.BLINDNESS).forGetter(PhasingPower::getRenderType))
		.and(Codec.floatRange(2.0F, Float.MAX_VALUE).optionalFieldOf("view_distance", 8.0F).forGetter(PhasingPower::getViewDistance))
		.apply(instance, PhasingPower::new));

	public static final PacketCodec<RegistryByteBuf, PhasingPower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.PACKET_CODEC), Power::getActiveCondition,
		Condition.PACKET_CODEC, PhasingPower::getPhaseDownCondition,
		RenderType.PACKET_CODEC, PhasingPower::getRenderType,
		PacketCodecs.FLOAT, PhasingPower::getViewDistance,
		PhasingPower::new
	);

	private final Condition phaseDownCondition;
	private final RenderType renderType;

	private final float viewDistance;

	public PhasingPower(Optional<Condition> activeCondition, Condition phaseDownCondition, RenderType renderType, float viewDistance) {
		super(activeCondition);
		this.phaseDownCondition = phaseDownCondition;
		this.renderType = renderType;
		this.viewDistance = viewDistance;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.PHASING;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getPhaseDownCondition().validate(reporter.makeChild(".phase_down_condition"));
	}

	public static class Instance extends Power.Instance<PhasingPower> {

		protected Instance(@NotNull Entity holder, @NotNull PhasingPower power) {
			super(holder, power);
		}

		public RenderType getRenderType() {
			return this.getPower().getRenderType();
		}

		public float getViewDistance() {
			return this.getPower().getViewDistance();
		}

		public boolean shouldPhaseDown(Context context, VoxelShape shape) {
			BlockPos blockPos = context.required(NeoApoliContextParameters.BLOCK_POS);
			return holder.getY() < (double) blockPos.getY() + shape.getMax(Direction.Axis.Y) - (holder.isOnGround() ? 8.05 / 16.0 : 0.0015)
				|| power.getPhaseDownCondition().test(context.makeChild(".phase_down_condition"));
		}

	}

	public static float getViewDistanceOrElse(Context context, FloatSupplier defaultValue) {

		Entity entity = context.nullable(NeoApoliContextParameters.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(entity, Instance.class, instance -> instance.getRenderType() == RenderType.BLINDNESS);

		boolean init = false;
		float result = defaultValue.getAsFloat();

		for (var instance : instances) {

			try {

				if (context.markActive(instance) && instance.isActive(context)) {

					if (init) {
						result = Math.min(result, instance.getViewDistance());
					}

					else {
						result = instance.getViewDistance();
						init = true;
					}

				}

			}

			finally {
				context.markInActive(instance);
			}

		}

		return result;

	}

	public static Context createContext(Entity entity, SavedBlockPosition savedBlock) {
		return PowerTypes.PHASING.contextBuilder()
			.add(NeoApoliContextParameters.BLOCK_POS, savedBlock.getBlockPos())
			.add(NeoApoliContextParameters.BLOCK_STATE, savedBlock.getBlockState())
			.addNullable(NeoApoliContextParameters.BLOCK_ENTITY, savedBlock.getBlockEntity())
			.add(NeoApoliContextParameters.THIS_ENTITY, entity)
			.add(NeoApoliContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

	public enum RenderType implements StringIdentifiable {

		BLINDNESS("blindness"),
		NONE("none");

		public static final Codec<RenderType> CODEC = CodecUtil.enumType(RenderType.class);
		public static final PacketCodec<ByteBuf, RenderType> PACKET_CODEC = PacketCodecUtil.enumType(RenderType.class);

		private final String name;

		RenderType(String name) {
			this.name = name;
		}

		@Override
		public String asString() {
			return name;
		}

	}

}
