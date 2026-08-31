package io.github.eggohito.neo_apoli.network.chat.contents;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.mixin.access.TranslatableContentsAccessor;
import io.github.eggohito.neo_apoli.registry.NeoApoliComponentContents;
import lombok.Getter;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.TranslatableFormatException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@Getter
public class TranslatableContentsWithTextFallback extends TranslatableContents {

	public static final MapCodec<TranslatableContentsWithTextFallback> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("translate").forGetter(TranslatableContentsWithTextFallback::getKey),
		ComponentSerialization.CODEC.fieldOf("fallback").forGetter(TranslatableContentsWithTextFallback::getTextFallback),
		TranslatableContentsAccessor.getArgumentCodec().listOf().optionalFieldOf("with").forGetter(textContent -> TranslatableContentsAccessor.callAdjustArgs(textContent.getArgs()))
	).apply(instance, TranslatableContentsWithTextFallback::new));

	private final Component textFallback;

	public TranslatableContentsWithTextFallback(String key, Component textFallback, Object... args) {
		super(key, null, args);
		this.textFallback = textFallback;
	}

	public TranslatableContentsWithTextFallback(String key, Component textFallback, Optional<List<Object>> args) {
		this(key, textFallback, TranslatableContentsAccessor.callAdjustArgs(args));
	}

	@Override
	public @NotNull Type<?> type() {
		return NeoApoliComponentContents.TRANSLATABLE_WITH_TEXT_FALLBACK;
	}

	@Override
	protected void decompose() {

		Language language = Language.getInstance();
		if (language == decomposedWith) {
			return;
		}

		String key = this.getKey();
		this.decomposedWith = language;

		if (language.has(key)) {

			String translated = language.getOrDefault(key);
			ImmutableList.Builder<FormattedText> builder = new ImmutableList.Builder<>();

			try {
				((TranslatableContentsAccessor) this).callDecomposeTemplate(translated, builder::add);
				this.decomposedParts = builder.build();
			}

			catch (TranslatableFormatException te) {
				this.decomposedParts = ImmutableList.of(FormattedText.of(translated));
			}

		}

		else {
			this.decomposedParts = ImmutableList.of(textFallback);
		}

	}

}
