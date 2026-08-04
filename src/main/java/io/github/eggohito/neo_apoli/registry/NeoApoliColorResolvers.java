package io.github.eggohito.neo_apoli.registry;

import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Consumer;

//  Biome color resolvers from vanilla copied to be physical-side agnostic
public final class NeoApoliColorResolvers {

	public static final ColorResolver BIOME_DRY_FOLIAGE_COLOR  = (biome, x, y) -> biome.getDryFoliageColor();
	public static final ColorResolver BIOME_FOLIAGE_COLOR = (biome, x, y) -> biome.getFoliageColor();
	public static final ColorResolver BIOME_GRASS_COLOR = Biome::getGrassColor;
	public static final ColorResolver BIOME_WATER_COLOR = (biome, x, y) -> biome.getWaterColor();

	public static void registerAll(Consumer<ColorResolver> registrant) {
		registrant.accept(BIOME_DRY_FOLIAGE_COLOR);
		registrant.accept(BIOME_FOLIAGE_COLOR);
		registrant.accept(BIOME_GRASS_COLOR);
		registrant.accept(BIOME_WATER_COLOR);
	}

}
