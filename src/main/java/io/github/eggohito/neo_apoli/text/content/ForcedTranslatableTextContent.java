package io.github.eggohito.neo_apoli.text.content;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.mixin.access.TranslatableTextContentAccessor;
import net.minecraft.text.*;
import net.minecraft.util.Language;

import java.util.List;
import java.util.Optional;

public class ForcedTranslatableTextContent extends TranslatableTextContent {

	public static final MapCodec<ForcedTranslatableTextContent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("translate").forGetter(ForcedTranslatableTextContent::getKey),
		TextCodecs.CODEC.fieldOf("fallback").forGetter(ForcedTranslatableTextContent::getTextFallback),
		TranslatableTextContentAccessor.getArgumentCodec().listOf().optionalFieldOf("with").forGetter(textContent -> TranslatableTextContentAccessor.callToOptionalList(textContent.getArgs()))
	).apply(instance, ForcedTranslatableTextContent::new));

	public static final Type<ForcedTranslatableTextContent> TYPE = new Type<>(CODEC, "neo-apoli:forced_translatable_text");

	private final Text textFallback;

	public ForcedTranslatableTextContent(String key, Text textFallback, Object... args) {
		super(key, null, args);
		this.textFallback = textFallback;
	}

	public ForcedTranslatableTextContent(String key, Text textFallback, Optional<List<Object>> args) {
		this(key, textFallback, TranslatableTextContentAccessor.callToArray(args));
	}

	@Override
	public Type<?> getType() {
		return TYPE;
	}

	@Override
	protected void updateTranslations() {

		Language language = Language.getInstance();
		if (language == languageCache) {
			return;
		}

		String key = this.getKey();
		this.languageCache = language;

		if (language.hasTranslation(key)) {

			String translated = language.get(key);
			ImmutableList.Builder<StringVisitable> builder = new ImmutableList.Builder<>();

			try {
				((TranslatableTextContentAccessor) this).callForEachPart(translated, builder::add);
				this.translations = builder.build();
			}

			catch (TranslationException te) {
				this.translations = ImmutableList.of(StringVisitable.plain(translated));
			}

		}

		else {
			this.translations = ImmutableList.of(textFallback);
		}

	}

	public Text getTextFallback() {
		return textFallback;
	}

}
