package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

import java.util.Optional;

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

		Context propertyContext = context.makeChild("property");
		String propertyString = property().stringValue(propertyContext);

		Context valueContext = context.makeChild("value");
		String valueString = value().stringValue(valueContext);

		if (propertyContext.hasErrors() || valueContext.hasErrors()) {
			return false;
		}

		BlockState state = this.getBlockState(context);
		Property<?> property = state.getBlock().getStateManager().getProperty(propertyString);

		if (property == null) {
			context.getReporter().report("Block \"" + Registries.BLOCK.getId(state.getBlock()) + "\" does not have property \"" + propertyString + "\"!");
		}

		return property != null
			&& this.testValue(state, property, valueString);

	}

	@Override
	public void validate(ErrorReporter reporter) {
		property().validate(reporter.makeChild("property"));
		value().validate(reporter.makeChild("value"));
	}

	private <T extends Comparable<T>> boolean testValue(State<?, ?> state, Property<T> property, String valueString) {

		T value = state.get(property);
		Optional<T> parsedValue = property.parse(valueString);

		return parsedValue.isPresent()
			&& parsedValue.get().compareTo(value) == 0;

	}

}
