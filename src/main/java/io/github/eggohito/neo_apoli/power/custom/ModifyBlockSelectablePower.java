package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@EqualsAndHashCode
@Getter
public class ModifyBlockSelectablePower extends Power implements Prioritized<ModifyBlockSelectablePower> {

	public static final MapCodec<ModifyBlockSelectablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockSelectablePower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockSelectablePower::getPriority))
		.apply(instance, ModifyBlockSelectablePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockSelectablePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		BooleanProvider.STREAM_CODEC, ModifyBlockSelectablePower::getAllow,
		ByteBufCodecs.INT, ModifyBlockSelectablePower::getPriority,
		ModifyBlockSelectablePower::new
	);

	private final BooleanProvider allow;
	private final int priority;

	public ModifyBlockSelectablePower(Optional<Condition> activeCondition, BooleanProvider allow, int priority) {
		super(activeCondition);
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_SELECTABLE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(ProblemReporter reporter) {
		super.validate(reporter);
		getAllow().validate(reporter.forChild(".allow"));
	}

	public static class Instance extends Power.Instance<ModifyBlockSelectablePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyBlockSelectablePower power) {
			super(holder, power);
		}

		public boolean isAllowed(Context context) {
			return power.getAllow().next(context.makeChild(".allow"));
		}

	}

	public static VoxelShape modify(Context context, Supplier<@NotNull VoxelShape> defaultValue) {

		Entity holder = context.required(NeoApoliContextKeys.THIS_ENTITY);
		InstanceCollection<Instance> instances = new InstanceCollection<>(holder, Instance.class);

		return modify(context, instances, defaultValue);

	}

	public static VoxelShape modify(Context context, InstanceCollection<Instance> instances, Supplier<@NotNull VoxelShape> defaultValue) {

		for (var instance : instances) {

			ProblemReporter reporter = instance.createReporter();
			Context instanceContext = ContextImpl.of(context, builder -> builder.withReporter(reporter));

			try {

				if (instanceContext.markActive(instance) && instance.isActive(instanceContext)) {

					if (instance.isAllowed(instanceContext)) {
						return defaultValue.get();
					}

					else {
						return Shapes.empty();
					}

				}

			}

			finally {
				instanceContext.markInActive(instance);
			}

		}

		return defaultValue.get();

	}

	public static Context createContext(@NotNull Entity entity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_SELECTABLE.contextBuilder()
			.add(NeoApoliContextKeys.BLOCK_POS, blockPos)
			.add(NeoApoliContextKeys.BLOCK_STATE, blockState)
			.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, blockEntity)
			.add(NeoApoliContextKeys.THIS_ENTITY, entity)
			.add(NeoApoliContextKeys.THIS_POS, entity.position())
			.build(entity.level());
	}

}
