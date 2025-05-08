package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.StringProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.state.State;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

		String propertyString = property().stringValue(context.makeChild("property"));
		String valueString = value().stringValue(context.makeChild("value"));

		if (context.hasAnyErrors()) {
			return false;
		}

		World world = context.getWorld();
		BlockState state = context
			.optionalParameter(ContextParameters.BLOCK_STATE)
			.orElseGet(() -> world.getBlockState(BlockPos.ofFloored(context.requiredParameter(ContextParameters.POSITION))));

		Property<?> property = state.getBlock().getStateManager().getProperty(propertyString);
		boolean result = property != null && this.testValue(state, property, valueString);

		if (property == null) {
			context.getReporter().report("Block \"" + Registries.BLOCK.getId(state.getBlock()) + "\" does not have property \"" + propertyString + "\"!");
		}

		return result;

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
