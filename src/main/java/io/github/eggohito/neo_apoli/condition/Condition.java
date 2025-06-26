package io.github.eggohito.neo_apoli.condition;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.category.ConditionCategory;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import org.apache.commons.lang3.StringUtils;

public abstract class Condition implements ContextAware, StringDisplayable {

	public abstract ConditionType<?> getType();

	public abstract ConditionCategory<? extends Condition> getCategory();

	public boolean test(Context context) {

		ErrorReporter reporter = context.getReporter();

		String category = StringUtils.uncapitalize(this.getCategory().toString());
		String fullPath = reporter.getFullPath();

		boolean result = false;
		Exception exception = null;

		try {
			result = this.impl(context);
		}

		catch (Exception e) {
			exception = e;
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
