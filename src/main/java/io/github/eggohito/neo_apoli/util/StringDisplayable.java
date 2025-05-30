package io.github.eggohito.neo_apoli.util;

import org.apache.commons.lang3.StringUtils;

public interface StringDisplayable {

	String asDisplayString();

	default String asDisplayString(boolean capitalized) {
		String displayString = this.asDisplayString();
		return capitalized
			? StringUtils.capitalize(displayString)
			: StringUtils.uncapitalize(displayString);
	}

}
