package io.github.eggohito.neo_apoli.action.custom.block;

import com.google.common.collect.Streams;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import io.github.eggohito.neo_apoli.util.context.ServerContext;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

import java.util.Optional;
import java.util.stream.Stream;

public record ModifyBlockStatePropertyBlockAction(StringProvider property, Optional<StringProvider> value, Optional<BooleanProvider> cycle) implements BlockAction {

	private static final MapCodec<StringProvider> PROPERTY_CODEC = StringProvider.CODEC.fieldOf("property");
	private static final MapCodec<Optional<StringProvider>> VALUE_CODEC = StringProvider.CODEC.optionalFieldOf("value");
	private static final MapCodec<Optional<BooleanProvider>> CYCLE_CODEC = BooleanProvider.CODEC.optionalFieldOf("cycle");

	public static final MapCodec<ModifyBlockStatePropertyBlockAction> CODEC = new MapCodec<>() {

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

	public static final PacketCodec<RegistryByteBuf, ModifyBlockStatePropertyBlockAction> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, ModifyBlockStatePropertyBlockAction::property,
		PacketCodecs.optional(StringProvider.PACKET_CODEC), ModifyBlockStatePropertyBlockAction::value,
		PacketCodecs.optional(BooleanProvider.PACKET_CODEC), ModifyBlockStatePropertyBlockAction::cycle,
		ModifyBlockStatePropertyBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.MODIFY_BLOCK_STATE_PROPERTY;
	}

	@Override
	public void serverExecute(ServerContext context) {

		if (!context.hasParameter(ContextParameters.BLOCK_STATE)) {
			return;
		}

		ServerContext propertyContext = context.makeChild(".property");
		String propertyName = property().next(propertyContext);

		if (propertyContext.hasErrors() || propertyName.isEmpty()) {
			return;
		}

		BlockState blockState = context.required(ContextParameters.BLOCK_STATE);
		Property<?> property = blockState.getBlock().getStateManager().getProperty(propertyName);

		if (property != null) {

			ServerContext cycleContext = context.makeChild(".cycle");
			Optional<Boolean> cycle = cycle().map(provider -> provider.next(cycleContext));

			if (!cycleContext.hasErrors()) {

				if (cycle.orElse(false)) {
					blockState.cycle(property);
				}

				else {
					setValue(context, blockState, property);
				}

			}

		}

		else {
			context.getReporter().report("Block \"" + RegistryUtil.getId(Registries.BLOCK, blockState.getBlock()) + "\" does not have a state property with the name \"" + propertyName + "\"!");
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		BlockAction.super.validate(reporter);
		property().validate(reporter.makeChild(".property"));
		value().ifPresent(value -> value.validate(reporter.makeChild(".value")));
		cycle().ifPresent(cycle -> cycle.validate(reporter.makeChild(".cycle")));
	}

	private <T extends Comparable<T>> void setValue(ServerContext context, State<?, ?> state, Property<T> property) {

		ServerContext valueContext = context.makeChild(".value");
		Optional<T> value = value()
			.map(provider -> provider.next(valueContext))
			.flatMap(property::parse);

		if (!valueContext.hasErrors() && value.isPresent()) {
			state.with(property, value.get());
		}

	}

}
