package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.PrioritizedPower;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyBlockSelectablePower extends Power implements PrioritizedPower<ModifyBlockSelectablePower> {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

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
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_BLOCK_SELECTABLE;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getAllow().validate(validator.forChild(".allow"));
	}

	public static class Instance extends Power.Instance<ModifyBlockSelectablePower> {

		protected Instance(@NotNull ModifyBlockSelectablePower power) {
			super(power);
		}

		public Context createContext(Entity holder, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.BLOCK, new CachedBlock(blockPos, blockState, blockEntity))
				.build(holder.level());
		}

		public boolean isAllowed(Context context) {
			return power.getAllow().getBoolean(context.forChild(".allow"));
		}

	}

	public static boolean shouldBeEmpty(Entity entity, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {

		for (var instance : new InstanceCollection<>(entity, Instance.class)) {

			Context context = instance.createContext(entity, blockPos, blockState, blockEntity);

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					return !instance.isAllowed(context);
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return false;

	}

}
