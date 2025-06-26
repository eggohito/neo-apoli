package io.github.eggohito.neo_apoli.action;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.category.ActionCategory;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.util.StringDisplayable;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import org.apache.commons.lang3.StringUtils;

public abstract class Action implements ContextAware, StringDisplayable {

	public abstract ActionType<?> getType();

	public abstract ActionCategory<?> getCategory();

	public void execute(Context context) {

		ErrorReporter reporter = context.getReporter();
		String fullPath = reporter.getFullPath();

		String category = StringUtils.uncapitalize(this.getCategory().toString());
		Exception exception = null;

		try {
			this.impl(context);
		}

		catch (Exception e) {
			exception = e;
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
