package io.github.eggohito.neo_apoli.client.impl.hud;

import com.google.common.collect.Iterators;
import io.github.eggohito.neo_apoli.client.hud.renderer.HudElementRenderer;
import io.github.eggohito.neo_apoli.client.hud.source.HudElementSource;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class HudElementHelperImpl {

	private static final Map<ResourceLocation, HudElementSource> SOURCES = new Object2ObjectLinkedOpenHashMap<>();
	private static final Map<ResourceLocation, HudElementRenderer> RENDERERS = new Object2ObjectLinkedOpenHashMap<>();

	private HudElementHelperImpl() {

	}

	public static Iterable<HudElementSource> getSources() {
		return () -> Iterators.unmodifiableIterator(SOURCES.values().iterator());
	}

	public static Iterable<HudElementRenderer> getRenderers() {
		return () -> Iterators.unmodifiableIterator(RENDERERS.values().iterator());
	}

	public static void registerRenderer(ResourceLocation id, HudElementRenderer renderer) {

		if (RENDERERS.putIfAbsent(id, renderer) != null) {
			throw new IllegalArgumentException("A HUD element renderer with the ID \"" + id + "\" is already registered!");
		}

	}

	public static void registerSource(ResourceLocation id, HudElementSource source) {

		if (SOURCES.putIfAbsent(id, source) != null) {
			throw new IllegalArgumentException("A HUD element source with the ID \"" + id + "\" is already registered!");
		}

	}

}
