package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public abstract class Action implements ContextAware, StringDisplayable {

	public static final MapCodec<Action> MAP_CODEC = ActionCategories.CODEC.dispatchMap("category", Action::getCategory, ActionCategory::mapCodec);

	public static final Codec<Action> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Action> PACKET_CODEC = ActionCategories.PACKET_CODEC.dispatch(Action::getCategory, ActionCategory::packetCodec);

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
