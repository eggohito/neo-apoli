package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.block.BlockProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

public record ModifyBlockStatePropertyAction(StringProvider property, Optional<StringProvider> value, Optional<BooleanProvider> cycle, BlockProvider block) implements Action {

	private static final MapCodec<ModifyBlockStatePropertyAction> UNVALIDATED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(ModifyBlockStatePropertyAction::property),
		StringProvider.CODEC.optionalFieldOf("value").forGetter(ModifyBlockStatePropertyAction::value),
		BooleanProvider.CODEC.optionalFieldOf("cycle").forGetter(ModifyBlockStatePropertyAction::cycle),
		BlockProvider.CODEC.fieldOf("block").forGetter(ModifyBlockStatePropertyAction::block)
	).apply(instance, ModifyBlockStatePropertyAction::new));

	public static final MapCodec<ModifyBlockStatePropertyAction> CODEC = UNVALIDATED_CODEC.mapResult(new MapCodec.ResultFunction<>() {

		@Override
		public <T> DataResult<ModifyBlockStatePropertyAction> apply(DynamicOps<T> ops, MapLike<T> input, DataResult<ModifyBlockStatePropertyAction> result) {
			return result.flatMap(
				action -> {

					var valueProp = action.value();
					var cycleProp = action.cycle();

					if (valueProp.isEmpty() && cycleProp.isEmpty()) {
						return DataResult.error(() -> "Any of 'value' or 'cycle' fields must be present in input: " + input);
					}

					else if (valueProp.isPresent() && cycleProp.isPresent()) {
						return DataResult.error(() -> "Both 'value' and 'cycle' fields cannot be present at the same time in input: " + input);
					}

					else {
						return DataResult.success(action);
					}

				}
			);
		}

		@Override
		public <T> RecordBuilder<T> coApply(DynamicOps<T> ops, ModifyBlockStatePropertyAction input, RecordBuilder<T> prefix) {
			return prefix;
		}

	});

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockStatePropertyAction> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, ModifyBlockStatePropertyAction::property,
		ByteBufCodecs.optional(StringProvider.STREAM_CODEC), ModifyBlockStatePropertyAction::value,
		ByteBufCodecs.optional(BooleanProvider.STREAM_CODEC), ModifyBlockStatePropertyAction::cycle,
		BlockProvider.STREAM_CODEC, ModifyBlockStatePropertyAction::block,
		ModifyBlockStatePropertyAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.MODIFY_BLOCK_STATE_PROPERTY;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		CachedBlock block = block().getBlock(context.forChild(".block")).orElse(null);

		if (block == null) {
			return;
		}

		BlockPos pos = block.pos();
		BlockState state = block.state();

		String propertyName = property().getString(context.forChild(".property"));
		Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);

		if (property == null) {
			context.reportProblem("Block \"" + Util.getRegisteredName(BuiltInRegistries.BLOCK, state.getBlock()) + "\" did not have the state property: \"" + propertyName + "\"");
		}

		else {

			boolean cycle = cycle()
				.map(self -> self.getBoolean(context.forChild(".cycle")))
				.orElse(false);

			if (cycle) {
				serverLevel.setBlockAndUpdate(pos, state.cycle(property));
			}

			else {
				this.setValue(serverLevel, context, pos, state, property);
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		property().validate(validator.forChild(".property"));
		value().ifPresent(value -> value.validate(validator.forChild(".value")));
		cycle().ifPresent(cycle -> cycle.validate(validator.forChild(".cycle")));
		block().validate(validator.forChild(".block"));
	}

	private <T extends Comparable<T>> void setValue(ServerLevel serverLevel, Context context, BlockPos pos, BlockState state, Property<T> property) {

		Optional<T> value = value()
			.map(self -> self.getString(context.forChild(".value")))
			.flatMap(property::getValue);

		if (value.isPresent()) {

			try {
				serverLevel.setBlockAndUpdate(pos, state.setValue(property, value.get()));
			}

			catch (IllegalArgumentException e) {
				context.reportProblem(e.getMessage());
			}

		}

	}

}
