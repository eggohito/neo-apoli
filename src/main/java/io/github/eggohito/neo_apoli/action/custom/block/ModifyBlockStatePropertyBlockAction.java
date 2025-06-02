package io.github.eggohito.neo_apoli.action.custom.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionType;
import io.github.eggohito.neo_apoli.action.type.block.BlockActionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public record ModifyBlockStatePropertyBlockAction(StringProvider property, Optional<StringProvider> value, boolean cycle) implements BlockAction {

	private static final MapCodec<ModifyBlockStatePropertyBlockAction> UNVALIDATED_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(ModifyBlockStatePropertyBlockAction::property),
		StringProvider.CODEC.optionalFieldOf("value").forGetter(ModifyBlockStatePropertyBlockAction::value),
		Codec.BOOL.optionalFieldOf("cycle", false).forGetter(ModifyBlockStatePropertyBlockAction::cycle)
	).apply(instance, ModifyBlockStatePropertyBlockAction::new));

	public static final MapCodec<ModifyBlockStatePropertyBlockAction> CODEC = UNVALIDATED_CODEC.flatXmap(
		blockAction -> {

			if (blockAction.value().isPresent() || blockAction.cycle()) {
				return DataResult.success(blockAction);
			}

			else {
				return DataResult.error(() -> "Either a 'value' has to be defined, or 'cycle' be defined as true!");
			}

		},
		DataResult::success
	);

	public static final PacketCodec<RegistryByteBuf, ModifyBlockStatePropertyBlockAction> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, ModifyBlockStatePropertyBlockAction::property,
		PacketCodecs.optional(StringProvider.PACKET_CODEC), ModifyBlockStatePropertyBlockAction::value,
		PacketCodecs.BOOLEAN, ModifyBlockStatePropertyBlockAction::cycle,
		ModifyBlockStatePropertyBlockAction::new
	);

	@Override
	public BlockActionType<?> getType() {
		return BlockActionTypes.MODIFY_BLOCK_STATE_PROPERTY;
	}

	@Override
	public void execute(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Context propertyContext = context.makeChild("property");
		String propertyString = property().stringValue(propertyContext);

		if (propertyContext.hasErrors()) {
			return;
		}

		BlockState state = context
			.optional(ContextParameters.BLOCK_STATE)
			.orElseGet(() -> serverWorld.getBlockState(BlockPos.ofFloored(context.required(ContextParameters.POSITION))));

		Property<?> property = state.getBlock().getStateManager().getProperty(propertyString);
		if (property != null) {

			if (cycle()) {
				state.cycle(property);
			}

			else {
				this.setValue(context, state, property);
			}

		}

		else {
			propertyContext.getReporter().report("Block \"" + Registries.BLOCK.getId(state.getBlock()) + "\" does not have property \"" + propertyString + "\"!");
		}

	}

	@Override
	public void validate(ErrorReporter reporter) {
		property().validate(reporter.makeChild("property"));
		value().ifPresent(value -> value.validate(reporter.makeChild("value")));
	}

	private <T extends Comparable<T>> void setValue(Context context, BlockState state, Property<T> property) {

		Context valueContext = context.makeChild("value");
		Optional<T> value = value()
			.map(provider -> provider.stringValue(valueContext))
			.flatMap(property::parse);

		if (value.isPresent() && !valueContext.hasErrors()) {
			state.with(property, value.get());
		}

	}

}
