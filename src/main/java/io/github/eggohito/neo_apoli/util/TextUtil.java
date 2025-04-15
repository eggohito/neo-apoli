package io.github.eggohito.neo_apoli.util;

import io.github.eggohito.neo_apoli.text.content.ForcedTranslatableTextContent;
import net.minecraft.text.*;

public class TextUtil {

	/**
	 * 	<p>Forcefully constructs a translatable text out of the {@code translationKey} and {@code altText} arguments.
	 * 	The resulting translatable text may differ according to the following scenarios:</p>
	 *
	 * 	<ol>
	 * 	    <li>
	 * 	        If {@code altText} is empty, a traditional translatable text will be constructed
	 * 	        (via {@link Text#translatable(String)}.)
	 * 	    </li>
	 * 	    <li>
	 * 	        If {@code altText} is already a translatable text, it will be used as is.
	 * 	    </li>
	 * 	    <li>
	 * 	        If {@code altText} is a literal string, the string will be used as a fallback translation for the
	 * 	        translatable text (constructed via {@link Text#translatableWithFallback(String, String)}.)
	 * 	    </li>
	 * 	    <li>
	 * 	        If neither of the above scenarios are inapplicable, a {@linkplain ForcedTranslatableTextContent forced
	 * 	        translatable text} will be constructed with {@code altText} serving as its fallback text.
	 * 	    </li>
	 * 	</ol>
	 *
	 * @param translationKey	the key of the translatable text that will be translated
	 * @param text			the text to use as a fallback if a traditional translatable text can't be constructed
	 *
	 * @return	either a traditional translatable text (if {@code altText} is not null, and a literal string, or a
	 * 			translatable text), or a {@link ForcedTranslatableTextContent}.
	 */
	public static Text forceTranslatable(String translationKey, Text text) {

		TextContent content = text.getContent();
		String literal = text.getLiteralString();

		if (content == PlainTextContent.EMPTY) {
			return Text.translatable(translationKey);
		}

		else if (content instanceof TranslatableTextContent) {
			return text;
		}

		else if (literal != null) {
			return Text.translatableWithFallback(translationKey, literal);
		}

		else {
			return MutableText.of(new ForcedTranslatableTextContent(translationKey, text));
		}

	}

}
