package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.context.ContextParameter;

import java.util.Optional;
import java.util.Set;

public record BlockStatePropertyBlockCondition(StringProvider property, StringProvider value) implements BlockCondition {

	public static final MapCodec<BlockStatePropertyBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(BlockStatePropertyBlockCondition::property),
		StringProvider.CODEC.fieldOf("value").forGetter(BlockStatePropertyBlockCondition::value)
	).apply(instance, BlockStatePropertyBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, BlockStatePropertyBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, BlockStatePropertyBlockCondition::property,
		StringProvider.PACKET_CODEC, BlockStatePropertyBlockCondition::value,
		BlockStatePropertyBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.BLOCK_STATE_PROPERTY;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasParameter(NeoApoliContextParameters.BLOCK_STATE)) {
			return false;
		}

		Context propertyContext = context.makeChild(".property");
		String propertyName = property().next(propertyContext);

		if (propertyContext.hasErrors()) {
			return false;
		}

		BlockState blockState = context.required(NeoApoliContextParameters.BLOCK_STATE);
		Property<?> property = blockState.getBlock().getStateManager().getProperty(propertyName);

		if (property == null) {
			propertyContext.getReporter().report("Block \"" + RegistryUtil.getId(Registries.BLOCK, blockState.getBlock()) + "\" doesn't have a block state property with name \"" + propertyName + "\"!");
		}

		return property != null
			&& this.testProperty(context, blockState, property);

	}

	@Override
	public Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParameters.BLOCK_STATE);
	}

	@Override
	public void validate(ErrorReporter reporter) {

		BlockCondition.super.validate(reporter);

		property().validate(reporter.makeChild(".property"));
		value().validate(reporter.makeChild(".value"));

	}

	private <T extends Comparable<T>> boolean testProperty(Context context, State<?, ?> state, Property<T> property) {

		Context valueContext = context.makeChild(".value");
		String unparsedValue = this.value().next(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		T currentValue = state.get(property);
		Optional<T> parsedValue = property.parse(unparsedValue);

		return parsedValue.isPresent()
			&& parsedValue.get().compareTo(currentValue) == 0;

	}

}
