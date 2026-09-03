package io.github.eggohito.neo_apoli.client.hud.internal;

import com.google.common.collect.Iterators;
import io.github.eggohito.neo_apoli.client.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.client.hud.source.HudElementSource;
import io.github.eggohito.neo_apoli.hud.element.HudElement;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.Util;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class HudElementHelperImpl {

	private static final Map<HudElement.Type<?>, HudElementRenderer<?>> RENDERERS = new Object2ObjectOpenHashMap<>();
	private static final Set<HudElementSource> SOURCES = new ObjectOpenHashSet<>();

	private HudElementHelperImpl() {

	}

	public static Iterable<HudElementSource> getSources() {
		return () -> Iterators.unmodifiableIterator(SOURCES.iterator());
	}

	@SuppressWarnings("unchecked")
	public static <E extends HudElement> HudElementRenderer<E> getRenderer(HudElement.Type<E> type) {
		return (HudElementRenderer<E>) Objects.requireNonNull(RENDERERS.get(type), "HUD element \"" + Util.getRegisteredName(NeoApoliRegistries.HUD_ELEMENT_TYPE, type) + "\" doesn't have a registered renderer!");
	}

	public static <E extends HudElement> void registerRenderer(HudElement.Type<E> type, HudElementRenderer<E> renderer) {

		if (RENDERERS.putIfAbsent(type, renderer) != null) {
			throw new IllegalArgumentException("A HUD element renderer for type \"" + Util.getRegisteredName(NeoApoliRegistries.HUD_ELEMENT_TYPE, type) + "\" is already registered!");
		}

	}

	public static void registerSource(HudElementSource source) {

		if (!SOURCES.add(source)) {
			throw new IllegalArgumentException("HUD element source " + source + " is already registered!");
		}

	}

}
