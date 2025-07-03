package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;

import java.util.Optional;

@EqualsAndHashCode
@Data
public final class BlockStatePropertyBlockCondition extends BlockCondition {

	public static final MapCodec<BlockStatePropertyBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(BlockStatePropertyBlockCondition::property),
		StringProvider.CODEC.fieldOf("value").forGetter(BlockStatePropertyBlockCondition::value)
	).apply(instance, BlockStatePropertyBlockCondition::new));

	public static final PacketCodec<RegistryByteBuf, BlockStatePropertyBlockCondition> PACKET_CODEC = PacketCodec.tuple(
		StringProvider.PACKET_CODEC, BlockStatePropertyBlockCondition::property,
		StringProvider.PACKET_CODEC, BlockStatePropertyBlockCondition::value,
		BlockStatePropertyBlockCondition::new
	);

	private final StringProvider property;
	private final StringProvider value;

	public BlockStatePropertyBlockCondition(StringProvider property, StringProvider value) {
		this.property = property;
		this.value = value;
	}

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.BLOCK_STATE_PROPERTY;
	}

	@Override
	protected boolean impl(Context context) {

		Context propertyContext = context.makeChild(".property");
		String propertyString = property().next(propertyContext);

		Context valueContext = context.makeChild(".value");
		String valueString = value().next(valueContext);

		if (propertyContext.hasErrors() || valueContext.hasErrors()) {
			return false;
		}

		BlockState state = this.getBlockState(context);
		Property<?> property = state.getBlock().getStateManager().getProperty(propertyString);

		if (property == null) {
			propertyContext.getReporter().report("Block \"" + Registries.BLOCK.getId(state.getBlock()) + "\" does not have property \"" + propertyString + "\"!");
		}

		return property != null
			&& this.testValue(state, property, valueString);

	}

	@Override
	public void validate(ErrorReporter reporter) {

		super.validate(reporter);

		property().validate(reporter.makeChild(".property"));
		value().validate(reporter.makeChild(".value"));

	}

	private <T extends Comparable<T>> boolean testValue(State<?, ?> state, Property<T> property, String valueString) {

		T value = state.get(property);
		Optional<T> parsedValue = property.parse(valueString);

		return parsedValue.isPresent()
			&& parsedValue.get().compareTo(value) == 0;

	}

}
