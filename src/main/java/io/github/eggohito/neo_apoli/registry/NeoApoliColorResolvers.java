package io.github.eggohito.neo_apoli.registry;

import net.minecraft.world.level.ColorResolver;

import java.util.function.Consumer;

public final class NeoApoliColorResolvers {

	//  A physical side-agnostic color resolver for getting the biome's water color
	public static final ColorResolver BIOME_WATER_COLOR = (biome, x, y) -> biome.getWaterColor();

	public static void registerAll(Consumer<ColorResolver> registrant) {
		registrant.accept(BIOME_WATER_COLOR);
	}

}
