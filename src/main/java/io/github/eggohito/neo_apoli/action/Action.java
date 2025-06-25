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

	public final void execute(Context context) {

		ErrorReporter reporter = context.getReporter();
		this.impl(context);

		if (reporter.isRoot() && reporter.hasAnyErrors()) {
			NeoApoli.LOGGER.warn("Couldn't execute {} at path {} properly due to error(s) {}", StringUtils.uncapitalize(this.getCategory().toString()), reporter.getFullPath(), reporter.getErrorsAsString());
		}

	}

	protected abstract void impl(Context context);

}
