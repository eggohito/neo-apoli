package io.github.eggohito.neo_apoli.client.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import net.fabricmc.loader.api.FabricLoader;

public class NeoApoliClientConfig {

	public static final ConfigClassHandler<NeoApoliClientConfig> HANDLER = ConfigClassHandler.createBuilder(NeoApoliClientConfig.class)
		.id(NeoApoli.id("config/client"))
		.serializer(handler -> GsonConfigSerializerBuilder.create(handler)
			.setPath(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/client.json5"))
			.setJson5(true).build()).build();

	@SerialEntry
	public final ResourceBars resourceBars = new ResourceBars();

	public static class ResourceBars {

		@SerialEntry
		public int offsetX = 0;

		@SerialEntry
		public int offsetY = 0;

	}

}
