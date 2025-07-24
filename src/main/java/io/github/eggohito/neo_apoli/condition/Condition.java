package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategories;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.event.Level;

import java.util.stream.Stream;

public abstract class Condition implements ContextAware, StringDisplayable {

	@SuppressWarnings("unchecked")
	public static final MapCodec<Condition> MAP_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of("category", "condition").map(ops::createString);
		}

		@Override
		public <T> DataResult<Condition> decode(DynamicOps<T> ops, MapLike<T> input) {
			return ConditionCategories.CODEC.fieldOf("category").decode(ops, input)
				.map(category -> (ConditionCategory<Condition>) category)
				.flatMap(category -> category.baseCodec().fieldOf("condition").decode(ops, input));
		}

		@Override
		public <T> RecordBuilder<T> encode(Condition input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			ConditionCategory<Condition> category = (ConditionCategory<Condition>) input.getCategory();
			return prefix
				.add("category", ConditionCategories.CODEC.encodeStart(ops, category))
				.add("condition", category.baseCodec().encodeStart(ops, input));
		}

	};

	@SuppressWarnings("unchecked")
	public static final PacketCodec<RegistryByteBuf, Condition> PACKET_CODEC = new PacketCodec<>() {

		@Override
		public Condition decode(RegistryByteBuf buf) {
			ConditionCategory<Condition> category = (ConditionCategory<Condition>) ConditionCategories.PACKET_CODEC.decode(buf);
			return category.basePacketCodec().decode(buf);
		}

		@Override
		public void encode(RegistryByteBuf buf, Condition value) {

			ConditionCategory<Condition> category = (ConditionCategory<Condition>) value.getCategory();

			ConditionCategories.PACKET_CODEC.encode(buf, category);
			category.basePacketCodec().encode(buf, value);

		}

	};

	public abstract ConditionType<?> getType();

	public abstract ConditionCategory<? extends Condition> getCategory();

	public boolean test(Context context) {

		ErrorReporter reporter = context.getReporter();

		String category = StringUtils.uncapitalize(this.getCategory().toString());
		String fullPath = reporter.getFullPath();

		boolean result = false;
		Exception exception = null;

		try {

			if (context.markActive(this)) {
				result = this.impl(context);
			}

			else {
				NeoApoli.logOnce(Level.WARN, "Recursively tested " + category + " at path " + fullPath + "!");
			}

		}

		catch (Exception e) {
			exception = e;
		}

		finally {
			context.markInactive(this);
		}

		if (exception != null || (reporter.isRoot() && reporter.hasAnyErrors())) {

			if (exception != null) {
				NeoApoli.LOGGER.error("Critical error trying to test {} at path {}: {}", category, fullPath, exception);
			}

			else {
				NeoApoli.LOGGER.warn("Couldn't properly test {} at path {} due to error(s) {}", category, fullPath, reporter.getErrorsAsString());
			}

		}

		return result;

	}

	protected abstract boolean impl(Context context);

}
