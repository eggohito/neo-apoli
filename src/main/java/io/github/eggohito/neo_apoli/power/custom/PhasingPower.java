package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.entity.IsSneakingEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.meta.TestEntityMetaCondition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.FloatSupplier;
import io.github.eggohito.neo_apoli.util.SavedBlockPosition;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import io.netty.buffer.ByteBuf;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;

@EqualsAndHashCode
@Getter
public class PhasingPower extends Power {

	public static final MapCodec<PhasingPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("phase_down_condition", new TestEntityMetaCondition(new IsSneakingEntityCondition(), NeoApoliContextKeys.THIS_ENTITY)).forGetter(PhasingPower::getPhaseDownCondition))
		.and(RenderType.CODEC.optionalFieldOf("render_type", RenderType.BLINDNESS).forGetter(PhasingPower::getRenderType))
		.and(Codec.floatRange(2.0F, Float.MAX_VALUE).optionalFieldOf("view_distance", 8.0F).forGetter(PhasingPower::getViewDistance))
		.apply(instance, PhasingPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, PhasingPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Condition.STREAM_CODEC, PhasingPower::getPhaseDownCondition,
		RenderType.STREAM_CODEC, PhasingPower::getRenderType,
		ByteBufCodecs.FLOAT, PhasingPower::getViewDistance,
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
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getPhaseDownCondition().validate(validator.forChild(".phase_down_condition"));
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

		public boolean shouldPhase(Context context, VoxelShape collisionShape) {
			BlockPos blockPos = context.required(NeoApoliContextKeys.BLOCK_POS);
			return holder.getY() < (double) blockPos.getY() + collisionShape.max(Direction.Axis.Y) - (holder.onGround() ? 8.05 / 16.0 : 0.0015)
				|| power.getPhaseDownCondition().test(context.forChild(".phase_down_condition"));
		}

	}

	public static boolean shouldPhase(Context context, VoxelShape collisionShape) {
		return shouldPhase(context, (instance, ctx) -> instance.isActive(ctx) && instance.shouldPhase(ctx, collisionShape));
	}

	public static boolean shouldPhase(Context context, BiPredicate<Instance, Context> tester) {

		Entity holder = context.nullable(NeoApoliContextKeys.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class);

		for (var instance : instances) {

			Context.Validator validator = instance.createValidator();
			Context instanceContext = new Context.Builder(context)
				.withValidator(validator)
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && tester.test(instance, context)) {
					return true;
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return false;

	}

	public static float getViewDistanceOrElse(Context context, FloatSupplier defaultValue) {

		Entity holder = context.nullable(NeoApoliContextKeys.THIS_ENTITY);
		List<Instance> instances = PowersComponent.getInstances(holder, Instance.class, instance -> instance.getRenderType() == RenderType.BLINDNESS);

		float result = defaultValue.getAsFloat();

		for (var instance : instances) {

			Context.Validator validator = instance.createValidator();
			Context instanceContext = new Context.Builder(context)
				.withValidator(validator)
				.build(context.getLevel());

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {
					result = Math.min(result, instance.getViewDistance());
				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return result;

	}

	public static Context createContext(Entity entity, SavedBlockPosition savedBlock) {
		return PowerTypes.PHASING.contextBuilder()
			.add(NeoApoliContextKeys.BLOCK_POS, savedBlock.getPos())
			.add(NeoApoliContextKeys.BLOCK_STATE, savedBlock.getState())
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, savedBlock.getEntity())
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

	public enum RenderType implements StringRepresentable {

		BLINDNESS("blindness"),
		NONE("none");

		public static final Codec<RenderType> CODEC = CodecUtil.enumType(RenderType.class);
		public static final StreamCodec<ByteBuf, RenderType> STREAM_CODEC = StreamCodecUtil.enumType(RenderType.class);

		private final String name;

		RenderType(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}

	}

}
