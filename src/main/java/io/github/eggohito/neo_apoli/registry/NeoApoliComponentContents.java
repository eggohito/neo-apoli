package io.github.eggohito.neo_apoli.registry;

import io.github.eggohito.neo_apoli.api.event.ComponentContentsRegistration;
import io.github.eggohito.neo_apoli.network.chat.contents.TranslatableContentsWithTextFallback;

public final class NeoApoliComponentContents {

	public static void registerAll() {
		ComponentContentsRegistration.EVENT.register(registrant -> registrant.accept(TranslatableContentsWithTextFallback.TYPE));
	}

}
