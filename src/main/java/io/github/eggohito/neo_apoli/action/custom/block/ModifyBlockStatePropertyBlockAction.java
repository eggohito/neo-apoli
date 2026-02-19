package io.github.eggohito.neo_apoli.action.custom.block;

import com.google.common.collect.Streams;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
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
import java.util.stream.Stream;

public record ModifyBlockStatePropertyBlockAction(StringProvider property, Optional<StringProvider> value, Optional<BooleanProvider> cycle) implements BlockAction {

	private static final MapCodec<StringProvider> PROPERTY_CODEC = StringProvider.CODEC.fieldOf("property");
	private static final MapCodec<Optional<StringProvider>> VALUE_CODEC = StringProvider.CODEC.optionalFieldOf("value");
	private static final MapCodec<Optional<BooleanProvider>> CYCLE_CODEC = BooleanProvider.CODEC.optionalFieldOf("cycle");

	public static final MapCodec<ModifyBlockStatePropertyBlockAction> MAP_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Streams.concat(PROPERTY_CODEC.keys(ops), VALUE_CODEC.keys(ops), CYCLE_CODEC.keys(ops));
		}

		@Override
		public <I> DataResult<ModifyBlockStatePropertyBlockAction> decode(DynamicOps<I> ops, MapLike<I> input) {
			return CYCLE_CODEC.decode(ops, input)
				.flatMap(cycle -> VALUE_CODEC.decode(ops, input)
					.flatMap(value -> PROPERTY_CODEC.decode(ops, input)
						.flatMap(property -> this.validate(property, value, cycle, input))));
		}

		@Override
		public <O> RecordBuilder<O> encode(ModifyBlockStatePropertyBlockAction input, DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return CYCLE_CODEC
				.encode(input.cycle(), ops, VALUE_CODEC
					.encode(input.value(), ops, PROPERTY_CODEC
						.encode(input.property(), ops, prefix)));
		}

		private <I> DataResult<ModifyBlockStatePropertyBlockAction> validate(StringProvider property, Optional<StringProvider> value, Optional<BooleanProvider> cycle, MapLike<I> input) {

			if (value.isEmpty() && cycle.isEmpty()) {
				return DataResult.error(() -> "Any of 'value' or 'cycle' keys must be present in input: " + input);
			}

			else {
				return DataResult.success(new ModifyBlockStatePropertyBlockAction(property, value, cycle));
			}

		}

	};

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyBlockStatePropertyBlockAction> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, ModifyBlockStatePropertyBlockAction::property,
		ByteBufCodecs.optional(StringProvider.STREAM_CODEC), ModifyBlockStatePropertyBlockAction::value,
		ByteBufCodecs.optional(BooleanProvider.STREAM_CODEC), ModifyBlockStatePropertyBlockAction::cycle,
		ModifyBlockStatePropertyBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.MODIFY_BLOCK_STATE_PROPERTY;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasAllParameters(this.getRequiredParameters())) {
			return;
		}

		BlockPos blockPos = context.getRequired(NeoApoliContextParams.BLOCK_POS);
		BlockState blockState = serverLevel.getBlockState(blockPos);

		String propertyName = property().nextString(context.forChild(".property"));
		Property<?> property = blockState.getBlock().getStateDefinition().getProperty(propertyName);

		if (property == null) {
			context.reportProblem("Block \"" + Util.getRegisteredName(BuiltInRegistries.BLOCK, blockState.getBlock()) + "\" did not have the state property: \"" + propertyName + "\"");
		}

		else {

			boolean cycle = cycle()
				.map(provider -> provider.nextBoolean(context.forChild(".cycle")))
				.orElse(false);

			if (cycle) {
				serverLevel.setBlockAndUpdate(blockPos, blockState.cycle(property));
			}

			else {
				this.setValue(context, blockState, property);
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		BlockAction.super.validate(validator);
		property().validate(validator.forChild(".property"));
		value().ifPresent(value -> value.validate(validator.forChild(".value")));
		cycle().ifPresent(cycle -> cycle.validate(validator.forChild(".cycle")));
	}

	private <T extends Comparable<T>> void setValue(Context context, BlockState state, Property<T> property) {

		Optional<T> value = value()
			.map(provider -> provider.nextString(context.forChild(".value")))
			.flatMap(property::getValue);

		if (value.isPresent()) {

			try {
				state.setValue(property, value.get());
			}

			catch (IllegalArgumentException e) {
				context.reportProblem("Error trying to set value of state property \"" + property.getName() + "\" from block \"" + Util.getRegisteredName(BuiltInRegistries.BLOCK, state.getBlock()) + "\"!");
			}

		}

	}

}
