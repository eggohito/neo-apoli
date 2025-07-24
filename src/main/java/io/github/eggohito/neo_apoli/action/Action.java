package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.category.ActionCategories;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public abstract class Action implements ContextAware, StringDisplayable {

	@SuppressWarnings("unchecked")
	public static final MapCodec<Action> MAP_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.of("category", "action").map(ops::createString);
		}

		@Override
		public <T> DataResult<Action> decode(DynamicOps<T> ops, MapLike<T> input) {
			return ActionCategories.CODEC.fieldOf("category").decode(ops, input)
				.map(category -> (ActionCategory<Action>) category)
				.flatMap(category -> category.baseCodec().fieldOf("action").decode(ops, input));
		}

		@Override
		public <T> RecordBuilder<T> encode(Action input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
			ActionCategory<Action> category = (ActionCategory<Action>) input.getCategory();
			return prefix
				.add("category", ActionCategories.CODEC.encodeStart(ops, category))
				.add("action", category.baseCodec().encodeStart(ops, input));
		}

	};

	@SuppressWarnings("unchecked")
	public static final PacketCodec<RegistryByteBuf, Action> PACKET_CODEC = new PacketCodec<>() {

		@Override
		public Action decode(RegistryByteBuf buf) {
			ActionCategory<Action> category = (ActionCategory<Action>) ActionCategories.PACKET_CODEC.decode(buf);
			return category.basePacketCodec().decode(buf);
		}

		@Override
		public void encode(RegistryByteBuf buf, Action value) {

			ActionCategory<Action> category = (ActionCategory<Action>) value.getCategory();

			ActionCategories.PACKET_CODEC.encode(buf, category);
			category.basePacketCodec().encode(buf, value);

		}

	};

	public abstract ActionType<?> getType();

	public abstract ActionCategory<?> getCategory();

	public void execute(Context context) {

		ErrorReporter reporter = context.getReporter();
		String fullPath = reporter.getFullPath();

		String category = StringUtils.uncapitalize(this.getCategory().toString());
		Exception exception = null;

		try {

			if (context.markActive(this)) {
				this.impl(context);
			}

			else {
				NeoApoli.LOGGER.warn("Recursively executed {} at path {}!", category, fullPath);
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
				NeoApoli.LOGGER.error("Critical error trying to execute {} at path {}: {}", category, fullPath, exception);
			}

			else {
				NeoApoli.LOGGER.warn("Couldn't properly execute {} at path {} due to error(s) {}", category, fullPath, reporter.getErrorsAsString());
			}

		}

	}

	protected abstract void impl(Context context);

}
