package io.github.eggohito.neo_apoli.condition.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.block.BlockProvider;
import io.github.eggohito.neo_apoli.provider.custom.string.StringProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliConditionTypes;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

public record BlockStatePropertyCondition(StringProvider property, StringProvider value, BlockProvider block) implements Condition {

	public static final MapCodec<BlockStatePropertyCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		StringProvider.CODEC.fieldOf("property").forGetter(BlockStatePropertyCondition::property),
		StringProvider.CODEC.fieldOf("value").forGetter(BlockStatePropertyCondition::value),
		BlockProvider.CODEC.fieldOf("block").forGetter(BlockStatePropertyCondition::block)
	).apply(instance, BlockStatePropertyCondition::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockStatePropertyCondition> STREAM_CODEC = StreamCodec.composite(
		StringProvider.STREAM_CODEC, BlockStatePropertyCondition::property,
		StringProvider.STREAM_CODEC, BlockStatePropertyCondition::value,
		BlockProvider.STREAM_CODEC, BlockStatePropertyCondition::block,
		BlockStatePropertyCondition::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliConditionTypes.BLOCK_STATE_PROPERTY;
	}

	@Override
	public boolean test(Context context) {

		Context blockContext = context.forChild(".block");
		CachedBlock block = block().getBlock(blockContext).orElse(null);

		if (blockContext.hasErrors() || block == null) {
			return false;
		}

		Context propertyContext = context.forChild(".property");
		String propertyName = property().getString(propertyContext);

		if (propertyContext.hasErrors() || propertyName.isEmpty()) {
			return false;
		}

		BlockState state = block.state();
		Property<?> property = state.getBlock().getStateDefinition().getProperty(propertyName);

		if (property == null) {
			context.reportProblem("Block \"" + RegistryUtil.getId(BuiltInRegistries.BLOCK, state.getBlock()) + "\" doesn't have the block state property with name \"" + propertyName + "\"!");
		}

		return property != null
			&& this.testProperty(context, state, property);

	}

	@Override
	public void validate(Context.Validator validator) {
		Condition.super.validate(validator);
		property().validate(validator.forChild(".property"));
		value().validate(validator.forChild(".value"));
		block().validate(validator.forChild(".block"));
	}

	private <T extends Comparable<T>> boolean testProperty(Context context, StateHolder<?, ?> state, Property<T> property) {

		Context valueContext = context.forChild(".value");
		String unparsedValue = value().getString(valueContext);

		if (valueContext.hasErrors()) {
			return false;
		}

		T value = state.getValue(property);
		Optional<T> parsedValue = property.getValue(unparsedValue);

		return parsedValue.isPresent()
			&& parsedValue.get().compareTo(value) == 0;

	}

}
