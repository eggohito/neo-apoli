package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
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

	public static final MapCodec<BlockStatePropertyBlockCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(BlockStatePropertyBlockCondition::property),
		StringProvider.CODEC.fieldOf("value").forGetter(BlockStatePropertyBlockCondition::value)
	).apply(instance, BlockStatePropertyBlockCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockStatePropertyBlockCondition> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, BlockStatePropertyBlockCondition::property,
		StringProvider.STREAM_CODEC, BlockStatePropertyBlockCondition::value,
		BlockStatePropertyBlockCondition::new
	);

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.BLOCK_STATE_PROPERTY;
	}

	@Override
	public boolean test(Context context) {

		if (!context.hasParameter(NeoApoliContextParams.BLOCK_STATE)) {
			return false;
		}

		Context propertyContext = context.forChild(".property");
		String propertyName = property().nextString(propertyContext);

		if (propertyContext.hasErrors() || propertyName.isEmpty()) {
			return false;
		}

		BlockState blockState = context.getRequired(NeoApoliContextParams.BLOCK_STATE);
		Property<?> property = blockState.getBlock().getStateDefinition().getProperty(propertyName);

		if (property == null) {
			propertyContext.forChild(".property").reportProblem("Block \"" + RegistryUtil.getId(BuiltInRegistries.BLOCK, blockState.getBlock()) + "\" doesn't have a block state property with name \"" + propertyName + "\"!");
		}

		return property != null
			&& this.testProperty(context, blockState, property);

	}

	@Override
	public Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(NeoApoliContextParams.BLOCK_STATE);
	}

	@Override
	public void validate(Context.Validator validator) {

		BlockCondition.super.validate(validator);

		property().validate(validator.forChild(".property"));
		value().validate(validator.forChild(".value"));

	}

	private <T extends Comparable<T>> boolean testProperty(Context context, StateHolder<?, ?> state, Property<T> property) {

		Context valueContext = context.forChild(".value");
		String unparsedValue = this.value().nextString(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		T currentValue = state.getValue(property);
		Optional<T> parsedValue = property.getValue(unparsedValue);

		return parsedValue.isPresent()
			&& parsedValue.get().compareTo(currentValue) == 0;

	}

}
