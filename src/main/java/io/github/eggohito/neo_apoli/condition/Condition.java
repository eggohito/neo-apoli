package io.github.eggohito.neo_apoli.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public abstract class Condition implements ContextAware, StringDisplayable {

	public static final MapCodec<Condition> MAP_CODEC = ConditionCategories.CODEC.dispatchMap("category", Condition::getCategory, ConditionCategory::mapCodec);

	public static final Codec<Condition> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Condition> PACKET_CODEC = ConditionCategories.PACKET_CODEC.dispatch(Condition::getCategory, ConditionCategory::packetCodec);

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
