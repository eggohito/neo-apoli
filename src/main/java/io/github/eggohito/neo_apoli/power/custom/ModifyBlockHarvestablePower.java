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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BooleanSupplier;

@EqualsAndHashCode
@Getter
public class ModifyBlockHarvestablePower extends Power implements PrioritizedPower<ModifyBlockHarvestablePower> {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyBlockHarvestablePower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
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
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_BLOCK_HARVESTABLE;
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

	public static class Instance extends Power.Instance<ModifyBlockHarvestablePower> {

		protected Instance(@NotNull ModifyBlockHarvestablePower power) {
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

	public static boolean modify(Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, BooleanSupplier defaultValue) {

		for (var instance : new InstanceCollection<>(player, Instance.class)) {

			Context context = instance.createContext(player, blockPos, blockState, blockEntity);

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
