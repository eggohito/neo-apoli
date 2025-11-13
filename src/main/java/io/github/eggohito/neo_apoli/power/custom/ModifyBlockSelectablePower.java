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
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

@Getter
public class ModifyBlockSelectablePower extends Power implements Prioritized<ModifyBlockSelectablePower> {

	public static final MapCodec<ModifyBlockSelectablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockSelectablePower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockSelectablePower::getPriority))
		.apply(instance, ModifyBlockSelectablePower::new));

	public static final PacketCodec<RegistryByteBuf, ModifyBlockSelectablePower> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.optional(Condition.BASE_PACKET_CODEC), Power::getActiveCondition,
		BooleanProvider.PACKET_CODEC, ModifyBlockSelectablePower::getAllow,
		PacketCodecs.INTEGER, ModifyBlockSelectablePower::getPriority,
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
	public void validate(ContextAware.ErrorReporter reporter) {
		super.validate(reporter);
		getAllow().validate(reporter.makeChild(".allow"));
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

		for (var instance : new InstanceCollection<>(context.nullable(ContextParameters.THIS_ENTITY), Instance.class)) {

			try {

				if (context.markActive(instance)) {

					if (instance.isActive(context)) {

						if (instance.isAllowed(context)) {
							return defaultValue.get();
						}

						else {
							return VoxelShapes.empty();
						}

					}

				}

			}

			finally {
				context.markInActive(instance);
			}

		}

		return defaultValue.get();

	}

	public static Context createContext(@NotNull Entity entity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
		return PowerTypes.MODIFY_BLOCK_SELECTABLE.contextBuilder()
			.add(ContextParameters.BLOCK_POS, blockPos)
			.add(ContextParameters.BLOCK_STATE, blockState)
			.addNullable(ContextParameters.BLOCK_ENTITY, blockEntity)
			.add(ContextParameters.THIS_ENTITY, entity)
			.add(ContextParameters.ENTITY_POS, entity.getPos())
			.build(entity.getWorld());
	}

}
