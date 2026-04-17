package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.api.event.ModifyValue;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.modifier.Modifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@EqualsAndHashCode
@Getter
public class ModifyBlockBreakSpeedPower extends Power {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

	public static final MapCodec<ModifyBlockBreakSpeedPower> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(ExtraCodecs.nonEmptyList(Modifier.CODEC.listOf()).fieldOf("modifiers").forGetter(ModifyBlockBreakSpeedPower::getModifiers))
		.apply(instance, ModifyBlockBreakSpeedPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockBreakSpeedPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		Modifier.STREAM_CODEC.apply(ByteBufCodecs.list()), ModifyBlockBreakSpeedPower::getModifiers,
		ModifyBlockBreakSpeedPower::new
	);

	private final List<Modifier> modifiers;

	public ModifyBlockBreakSpeedPower(Optional<Condition> activeCondition, List<Modifier> modifiers) {
		super(activeCondition);
		this.modifiers = modifiers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_BLOCK_BREAK_SPEED;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
	}

	public static class Instance extends Power.Instance<ModifyBlockBreakSpeedPower> {

		protected Instance(@NotNull ModifyBlockBreakSpeedPower power) {
			super(power);
		}

		public Context createContext(Entity holder, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity) {
			return this.createHolderContextBuilder(holder)
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, blockState)
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, blockEntity)
				.build(holder.level());
		}

		public List<Modifier.Entry> getModifiers(Context context) {

			List<Modifier.Entry> result = new ObjectArrayList<>();
			MiscUtil.iterateList(power.getModifiers(), (index, modifier) -> result.add(Modifier.entry(modifier, context.forChild(".modifiers[" + index + "]"))));

			return result;

		}

	}

	public static float modify(Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, float breakSpeed) {

		List<Modifier.Entry> entries = new ObjectArrayList<>();

		for (var instance : Powers.getInstances(player, Instance.class)) {

			try {

				Context context = instance.createContext(player, blockPos, blockState, blockEntity);

				if (VISITOR.push(instance) && instance.isActive(context)) {
					entries.addAll(instance.getModifiers(context));
				}

			}

			finally {
				VISITOR.pop(instance);
			}

		}

		ModifyValue.EVENT.invoker().beforeModified(PowerTypes.MODIFY_JUMP, entries, breakSpeed);
		return (float) Modifier.applyAll(entries, breakSpeed);

	}

}
