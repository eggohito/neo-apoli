package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.network.chat.contents.ForcedTranslatableContents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ComponentUtil {

	/**
	 * 	<p>Forcefully constructs a translatable text out of the {@code translationKey} and {@code altText} arguments.
	 * 	The resulting translatable text may differ according to the following scenarios:</p>
	 *
	 * 	<ol>
	 * 	    <li>
	 * 	        If {@code altText} is empty, a traditional translatable text will be constructed
	 * 	        (via {@link Component#translatable(String)}.)
	 * 	    </li>
	 * 	    <li>
	 * 	        If {@code altText} is already a translatable text, it will be used as is.
	 * 	    </li>
	 * 	    <li>
	 * 	        If {@code altText} is a literal string, the string will be used as a fallback translation for the
	 * 	        translatable text (constructed via {@link Component#translatableWithFallback(String, String)}.)
	 * 	    </li>
	 * 	    <li>
	 * 	        If neither of the above scenarios are inapplicable, a {@linkplain ForcedTranslatableContents forced
	 * 	        translatable text} will be constructed with {@code altText} serving as its fallback text.
	 * 	    </li>
	 * 	</ol>
	 *
	 * @param translationKey	the key of the translatable text that will be translated
	 * @param text			the text to use as a fallback if a traditional translatable text can't be constructed
	 *
	 * @return	either a traditional translatable text (if {@code altText} is not null, and a literal string, or a
	 * 			translatable text), or a {@link ForcedTranslatableContents}.
	 */
	public static Component forceTranslatable(String translationKey, Component text) {

		ComponentContents content = text.getContents();
		String literal = text.tryCollapseToString();

		if (content == PlainTextContents.EMPTY) {
			return Component.translatable(translationKey);
		}

		else if (content instanceof TranslatableContents) {
			return text;
		}

		else if (literal != null) {
			return Component.translatableWithFallback(translationKey, literal);
		}

		else {
			return MutableComponent.create(new ForcedTranslatableContents(translationKey, text));
		}

	}

}
