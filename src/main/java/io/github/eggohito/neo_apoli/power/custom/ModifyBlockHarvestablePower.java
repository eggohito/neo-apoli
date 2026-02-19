package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@EqualsAndHashCode
@Getter
public class ModifyBlockHarvestablePower extends Power implements Prioritized<ModifyBlockHarvestablePower> {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyBlockHarvestablePower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(BooleanProvider.CODEC.fieldOf("allow").forGetter(ModifyBlockHarvestablePower::getAllow))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyBlockHarvestablePower::getPriority))
		.apply(instance, ModifyBlockHarvestablePower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockHarvestablePower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		BooleanProvider.STREAM_CODEC, ModifyBlockHarvestablePower::getAllow,
		ByteBufCodecs.INT, ModifyBlockHarvestablePower::getPriority,
		ModifyBlockHarvestablePower::new
	);

	private final BooleanProvider allow;
	private final int priority;

	public ModifyBlockHarvestablePower(Optional<Condition> activeCondition, BooleanProvider allow, int priority) {
		super(activeCondition);
		this.allow = allow;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_HARVESTABLE;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		getAllow().validate(validator.forChild(".allow"));
	}

	public static class Instance extends Power.Instance<ModifyBlockHarvestablePower> {

		protected Instance(@NotNull Entity holder, @NotNull ModifyBlockHarvestablePower power) {
			super(holder, power);
		}

		public Context createContext(BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
			return this.createHolderContextBuilder()
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, blockState)
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, blockEntity)
				.buildWithRequirements(holder.level(), PowerTypes.MODIFY_BLOCK_HARVESTABLE.keySet());
		}

		public boolean isAllowed(Context context) {
			return power.getAllow().nextBoolean(context.forChild(".allow"));
		}

	}

	public static boolean modify(Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, BooleanSupplier defaultValue) {

		for (var instance : new InstanceCollection<>(player, Instance.class)) {

			Context context = instance.createContext(blockPos, blockState, blockEntity);

			try {

				if (VISITOR.push(instance) && instance.isActive(context)) {
					return instance.isAllowed(context);
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		return defaultValue.getAsBoolean();

	}

}
