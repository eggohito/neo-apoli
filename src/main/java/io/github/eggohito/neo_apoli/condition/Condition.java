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

	public final boolean test(Context context) {

		ErrorReporter reporter = context.getReporter();
		boolean result = this.impl(context);

		if (reporter.isRoot() && reporter.hasErrors()) {
			NeoApoli.LOGGER.warn("Couldn't test {} at path {} property due to error(s) {}", StringUtils.uncapitalize(this.getCategory().toString()), reporter.getFullPath(), reporter.getErrorsAsString());
		}

		return result;

	}

	protected abstract boolean impl(Context context);

}
