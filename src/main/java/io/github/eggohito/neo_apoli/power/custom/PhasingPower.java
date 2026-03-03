package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.component.entity.PowersComponent;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.TestEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.IsSneakingEntityCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
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

import java.util.Optional;
import java.util.function.BiPredicate;

@EqualsAndHashCode
@Getter
public class PhasingPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<PhasingPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Condition.CODEC.optionalFieldOf("phase_down_condition", new TestEntityCondition(IsSneakingEntityCondition.INSTANCE, NeoApoliContextParams.THIS_ENTITY)).forGetter(PhasingPower::getPhaseDownCondition))
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
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getPhaseDownCondition().validate(validator.forChild(".phase_down_condition"));
	}

	public static class Instance extends Power.Instance<PhasingPower> {

		protected Instance(@NotNull PhasingPower power) {
			super(power);
		}

		public Context createContext(Entity holder, CachedBlock cachedBlock) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.BLOCK_POS, cachedBlock.getPos())
				.withRequired(NeoApoliContextParams.BLOCK_STATE, cachedBlock.getState())
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, cachedBlock.getEntity())
				.buildWithRequirements(holder.level(), PowerTypes.PHASING.keySet());
		}

		public RenderType getRenderType() {
			return power.getRenderType();
		}

		public boolean doesApply(Entity holder, Context context, BlockPos blockPos, VoxelShape blockShape) {
			return holder.getY() < (double) blockPos.getY() + blockShape.max(Direction.Axis.Y) - (holder.onGround() ? 8.05 / 16.0 : 0.0015)
				|| power.getPhaseDownCondition().test(context.forChild(".phase_down_condition"));
		}

		public float getViewDistance() {
			return power.getViewDistance();
		}

	}

	public static boolean doesApply(Entity entity, CachedBlock cachedBlock, VoxelShape blockShape) {
		return doesApply(entity, cachedBlock, (instance, context) -> instance.isActive(context) && instance.doesApply(entity, context, cachedBlock.getPos(), blockShape));
	}

	public static boolean doesApply(Entity entity, CachedBlock cachedBlock, BiPredicate<Instance, Context> tester) {

		for (var instance : PowersComponent.getInstances(entity, Instance.class)) {

			Context context = instance.createContext(entity, cachedBlock);

			try {

				if (VISITOR.push(instance) && tester.test(instance, context)) {
					return true;
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return false;

	}

	public static float modifyViewDistance(Entity entity, CachedBlock cachedBlock, float viewDistance) {

		for (var instance : PowersComponent.getInstances(entity, Instance.class)) {

			Context context = instance.createContext(entity, cachedBlock);

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					viewDistance = Math.min(viewDistance, instance.getViewDistance());
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return viewDistance;

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
