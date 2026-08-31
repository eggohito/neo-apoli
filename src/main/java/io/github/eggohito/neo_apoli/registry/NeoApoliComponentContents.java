package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.event.ComponentContentsRegistration;
import io.github.eggohito.neo_apoli.network.chat.contents.TranslatableContentsWithTextFallback;
import net.minecraft.network.chat.ComponentContents;

public final class NeoApoliComponentContents {

	public static final ComponentContents.Type<TranslatableContentsWithTextFallback> TRANSLATABLE_WITH_TEXT_FALLBACK = registerInternal("translatable_with_text_fallback", TranslatableContentsWithTextFallback.CODEC);

	public static void registerAll() {

	}

	private static <C extends ComponentContents> ComponentContents.Type<C> registerInternal(String path, MapCodec<C> codec) {

		ComponentContents.Type<C> type = new ComponentContents.Type<>(codec, NeoApoli.id(path).toString());
		ComponentContentsRegistration.EVENT.register(registrant -> registrant.accept(type));

		return type;

	}

}
