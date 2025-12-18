package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionType;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;
import java.util.Set;

public record BlockStatePropertyBlockCondition(StringProvider property, StringProvider value) implements BlockCondition {

	public static final MapCodec<BlockStatePropertyBlockCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(BlockStatePropertyBlockCondition::property),
		StringProvider.CODEC.fieldOf("value").forGetter(BlockStatePropertyBlockCondition::value)
	).apply(instance, BlockStatePropertyBlockCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockStatePropertyBlockCondition> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, BlockStatePropertyBlockCondition::property,
		StringProvider.STREAM_CODEC, BlockStatePropertyBlockCondition::value,
		BlockStatePropertyBlockCondition::new
	);

	@Override
	public BlockConditionType<?> getType() {
		return BlockConditionTypes.BLOCK_STATE_PROPERTY;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasParameter(NeoApoliContextKeys.BLOCK_STATE)) {
			return false;
		}

		Context propertyContext = context.forChild(".property");
		String propertyName = property().next(propertyContext);

		if (propertyContext.hasErrors()) {
			return false;
		}

		BlockState blockState = context.required(NeoApoliContextKeys.BLOCK_STATE);
		Property<?> property = blockState.getBlock().getStateDefinition().getProperty(propertyName);

		if (property == null) {
			propertyContext.getValidator().report("Block \"" + RegistryUtil.getId(BuiltInRegistries.BLOCK, blockState.getBlock()) + "\" doesn't have a block state property with name \"" + propertyName + "\"!");
		}

		return property != null
			&& this.testProperty(context, blockState, property);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextKeys.BLOCK_STATE);
	}

	@Override
	public void validate(Context.Validator validator) {

		BlockCondition.super.validate(validator);

		property().validate(validator.forChild(".property"));
		value().validate(validator.forChild(".value"));

	}

	private <T extends Comparable<T>> boolean testProperty(Context context, StateHolder<?, ?> state, Property<T> property) {

		Context valueContext = context.forChild(".value");
		String unparsedValue = this.value().next(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		T currentValue = state.getValue(property);
		Optional<T> parsedValue = property.getValue(unparsedValue);

		return parsedValue.isPresent()
			&& parsedValue.get().compareTo(currentValue) == 0;

	}

}
